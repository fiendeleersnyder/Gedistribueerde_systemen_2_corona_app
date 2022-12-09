import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.*;

public class MatchingService_implementation extends UnicastRemoteObject implements MatchingService{
    Registry myRegistryRegistrar;
    Registry myRegistryMixingProxy;
    Registrar registrar;
    MixingProxy mixingProxy;
    PublicKey publicKeyDoctor;
    byte[] signatureDoctor;
    ArrayList <usedToken> infectedTokens;
    ArrayList<Capsule> capsules;
    ArrayList<Capsule> uninformedInfected;
    ArrayList<ArrayList<byte[]>> pseudonymen;
    JFrame frame;
    JButton b;
    JButton informedlogs;
    JButton uninformedUsers;


    public MatchingService_implementation() throws RemoteException, NotBoundException {
        myRegistryRegistrar = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistryRegistrar.lookup("Registrar");

        myRegistryMixingProxy = LocateRegistry.getRegistry("localhost", 9000, new SslRMIClientSocketFactory());
        mixingProxy = (MixingProxy) myRegistryMixingProxy.lookup("MixingProxy");

        infectedTokens = new ArrayList<>();
        capsules = new ArrayList<>();
        uninformedInfected = new ArrayList<>();
        pseudonymen = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            pseudonymen.add(new ArrayList<>());
        }

        frame= new JFrame("Matching Service");
        JPanel panel = new JPanel();
        b = new JButton("Flush mixing queue");
        b.addActionListener(e -> {
            try {
                getCapsules();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        informedlogs = new JButton("Get informed logs");
        informedlogs.addActionListener(e -> {
            try {
                ArrayList<usedToken> infectedFromMixing = mixingProxy.getInfectedTokens();
                ArrayList<Capsule> verwijderCapsules = new ArrayList<>();
                for(usedToken used: infectedFromMixing){
                    for(Capsule capsule : uninformedInfected){
                        for(usedToken token : infectedFromMixing){
                            if (Objects.equals(token.getHash(), capsule.getHash())){
                                verwijderCapsules.add(capsule);
                            }
                        }
                    }
                }
                for (Capsule capsule: verwijderCapsules) {
                    uninformedInfected.remove(capsule);
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        uninformedUsers = new JButton("Get uninformed users");
        uninformedUsers.addActionListener(e -> {
            try {
                registrar.sendUninformedUsers(uninformedInfected);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    getCapsules();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 1000*60*30, 1000*60*30);


        frame.setSize(300,300);
        b.setBounds(135,145,30,10);
        panel.add(b);
        panel.add(informedlogs);
        frame.add( panel );
        frame.setVisible(true);
    }

    public void uploadFileToMatchingServer(byte[] mydata,  byte[] signature, PublicKey publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        this.signatureDoctor = signature;
        this.publicKeyDoctor = publicKey;
        try {
            File serverpathfile = new File("logMatchingService.txt");
            FileOutputStream out=new FileOutputStream(serverpathfile);
            out.write(mydata);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(publicKeyDoctor);
        //file omzetten naar een byte array zodat deze gesigned kan worden
        File clientpathfile = new File("logMatchingService.txt");
        byte [] fileToSign=new byte[(int) clientpathfile.length()];
        FileInputStream in=new FileInputStream(clientpathfile);
        in.read(fileToSign, 0, fileToSign.length);
        in.close();

        sign.update(fileToSign);
        boolean signed = sign.verify(signatureDoctor);

        if(signed){
            System.out.println("Matching Service is done writing data...");
            //hier log.txt uitlezen
            File file = new File("logMatchingService.txt");
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()){
                String lijn = sc.nextLine();
                String[] split= lijn.split("%");
                LocalDateTime begin = LocalDateTime.parse(split[1]);
                LocalDateTime eind = LocalDateTime.parse(split[2]);
                String hash = split[3];
                int randomnummer = Integer.parseInt(split[4]);
                System.out.println(begin + " " + eind + " " +hash + " "+ randomnummer);
                infectedTokens.add(new usedToken(begin, eind, hash, randomnummer));
            }

            int day;
            boolean valid = false;
            ArrayList<usedToken> unvalidTokens = new ArrayList<>();
            for (usedToken token: infectedTokens) {
                day = token.getBeginTijd().getDayOfMonth();
                if (pseudonymen.get(day-1).isEmpty()) {
                    pseudonymen.get(day-1).addAll(registrar.getPseudonyms(day));
                }
                for (byte[] pseudonym: pseudonymen.get(day-1)) {
                    System.out.println(Arrays.toString(pseudonym));
                    byte[] gemaaktehash = makeHash(pseudonym, token.getRandomNumber());
                    System.out.println(Arrays.toString(gemaaktehash));
                    if (Objects.equals(token.getHash(), Arrays.toString(gemaaktehash))) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    unvalidTokens.add(token);
                }
            }

            for (usedToken token: unvalidTokens) {
                infectedTokens.remove(token);
            }


            if(capsules.size()!=0) {
                for (usedToken token : infectedTokens) {
                    ArrayList<Capsule> temp = capsules;
                    Iterator<Capsule> iterator = temp.iterator();
                    while (iterator.hasNext()) {
                        Capsule capsule = iterator.next();
                        if (Objects.equals(token.getHash(), capsule.getHash())) {
                            if (checkTimeInterval(token.getBeginTijd(), token.getEindTijd(), capsule.getLocalDateTime())) {
                                uninformedInfected.add(capsule);
                                iterator.remove();
                                System.out.println("found infected user");
                            }
                        }
                    }
                    capsules = temp;
                }
            }
        }
        else System.out.println("An error occurred: the signature provided by the doctor and the signature" +
                " generated by the matching service don't match.");
    }

    @Override
    public ArrayList<Capsule> getInfectedList() throws RemoteException {
        return uninformedInfected;
    }

    public void getCapsules() throws RemoteException {
        capsules = mixingProxy.getCapsules();
    }

    public byte[] makeHash(byte[] pseudonym, int random) throws NoSuchAlgorithmException {
        String data = random + "," + Arrays.toString(pseudonym);
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        md.update(data.getBytes(StandardCharsets.UTF_8));
        return md.digest();
    }

    public boolean checkTimeInterval(LocalDateTime begin, LocalDateTime eind, LocalDateTime user) {
        boolean inInterval = false;
        if(begin.compareTo(user) <= 0 & eind.compareTo(user) >= 0){
            inInterval = true;
        }
        return inInterval;
    }
}
