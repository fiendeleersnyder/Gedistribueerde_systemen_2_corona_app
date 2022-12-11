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
    JPanel panel;
    JPanel panel1;
    JLabel text;
    JLabel infected;
    JButton b;
    JButton informedlogs;
    JButton uninformedUsers;
    ArrayList<JLabel> labels;
    ArrayList<JLabel> labels2;

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
        labels = new ArrayList<>();
        labels2 = new ArrayList<>();

        frame= new JFrame("Matching Service");
        panel = new JPanel();
        text = new JLabel("Capsules: ");
        panel1 = new JPanel();
        infected = new JLabel("Uninformed capsules");

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
                if (!infectedFromMixing.isEmpty()) {
                    ArrayList<Capsule> verwijderCapsules = new ArrayList<>();
                    for (usedToken used : infectedFromMixing) {
                        for (Capsule capsule : uninformedInfected) {
                            if (capsule.getToken().getRandomNumber() == used.getToken().getRandomNumber()){
                                verwijderCapsules.add(capsule);
                            }
                        }
                    }
                    for (Capsule capsule : verwijderCapsules) {
                        uninformedInfected.remove(capsule);
                    }
                    updateUninformed();
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        uninformedUsers = new JButton("Get uninformed users");
        uninformedUsers.addActionListener(e -> {
            try {
                registrar.sendUninformedUsers(uninformedInfected);
                uninformedInfected.clear();
                updateUninformed();
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

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ArrayList<Capsule> deleteCapsule = new ArrayList<>();
                for (Capsule capsule: capsules) {
                    if (capsule.getLocalDateTime().plusMinutes(60*48).compareTo(LocalDateTime.now()) < 0) {
                        deleteCapsule.add(capsule);
                    }
                }
                for (Capsule capsule: deleteCapsule) {
                    capsules.remove(capsule);
                }
            }
        }, 1000*60*60*48, 1000*60*60*48);

        frame.setSize(1300,600);
        b.setBounds(135,145,30,10);
        panel.add(b);
        panel.add(informedlogs);
        panel.add(uninformedUsers);
        panel.add(text);
        panel1.add(infected);
        JSplitPane sl = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel, panel1);
        frame.add(sl);
        frame.setVisible(true);
    }

    public void updateGUI() {
        for (JLabel label: labels) {
            label.setText("");
        }
        if (!capsules.isEmpty()) {
            for (Capsule c: capsules) {
                JLabel capsule = new JLabel();
                capsule.setText("LocalTime: " + c.getLocalDateTime() + " Token: " + c.getToken() + " Hash: " + c.getHash());
                panel.add(capsule);
                labels.add(capsule);
            }
            frame.setVisible(true);
        }
    }

    public void updateUninformed() {
        for (JLabel label: labels2) {
            label.setText("");
        }
        if (!uninformedInfected.isEmpty()) {
            for (Capsule c: uninformedInfected) {
                JLabel capsule = new JLabel();
                capsule.setText("LocalTime: " + c.getLocalDateTime() + " Token: " + c.getToken() + " Hash: " + c.getHash());
                panel1.add(capsule);
                labels2.add(capsule);
            }
            frame.setVisible(true);
        }
    }

    public void uploadFileToMatchingServer(byte[] mydata, ArrayList<usedToken> gebruikteTokens, byte[] signature, PublicKey publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
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
            /*System.out.println("Matching Service is done writing data...");
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
            }*/

            int day;
            boolean valid = false;
            ArrayList<usedToken> unvalidTokens = new ArrayList<>();
            for (usedToken token: gebruikteTokens) {
                day = token.getBeginTijd().getDayOfMonth();
                if (pseudonymen.get(day-1).isEmpty()) {
                    pseudonymen.get(day-1).addAll(registrar.getPseudonyms(day));
                }
                for (byte[] pseudonym: pseudonymen.get(day-1)) {
                    byte[] gemaaktehash = makeHash(pseudonym, token.getRandomNumber());
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
                for (usedToken token : gebruikteTokens) {
                    System.out.println(token.getBeginTijd() + " " + token.getEindTijd() + " " + token.getToken() + " " + token.getHash());
                    ArrayList<Capsule> temp = capsules;
                    Iterator<Capsule> iterator = temp.iterator();
                    while (iterator.hasNext()) {
                        Capsule capsule = iterator.next();
                        if (Objects.equals(token.getHash(), capsule.getHash())) {
                            if (checkTimeInterval(token.getBeginTijd(), token.getEindTijd(), capsule.getLocalDateTime())) {
                                if (capsule.getToken().getRandomNumber() != token.getToken().getRandomNumber()) {
                                    uninformedInfected.add(capsule);
                                    iterator.remove();
                                    System.out.println("found infected user");
                                }
                            }
                        }
                    }
                    token.setInformed(true);
                    capsules = temp;
                }
                updateUninformed();
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
        capsules.addAll(mixingProxy.getCapsules());
        updateGUI();
    }

    public byte[] makeHash(byte[] pseudonym, int random) throws NoSuchAlgorithmException {
        String data = random + "," + Arrays.toString(pseudonym);
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        md.update(data.getBytes(StandardCharsets.UTF_8));
        return md.digest();
    }

    public boolean checkTimeInterval(LocalDateTime begin, LocalDateTime eind, LocalDateTime user) {
        return begin.compareTo(user) <= 0 & eind.compareTo(user) >= 0;
    }
}
