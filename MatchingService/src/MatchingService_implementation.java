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
import java.sql.Array;
import java.time.LocalTime;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MatchingService_implementation extends UnicastRemoteObject implements MatchingService{
    Registry myRegistryRegistrar;
    Registry myRegistryMixingProxy;
    Registrar registrar;
    MixingProxy mixingProxy;
    PublicKey publicKeyDoctor;
    byte[] signatureDoctor;
    ArrayList <byte []> pseudonymList;
    ArrayList <usedToken> infectedTokens;
    ArrayList<Capsule> capsules;
    ArrayList<Capsule> uninformedInfected;
    JFrame frame;//= new JFrame("Matching Service");
    JButton b;// = new JButton("Flush mixing queue");


    public MatchingService_implementation() throws RemoteException, NotBoundException {
        myRegistryRegistrar = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistryRegistrar.lookup("Registrar");

        myRegistryMixingProxy = LocateRegistry.getRegistry("localhost", 9000, new SslRMIClientSocketFactory());
        mixingProxy = (MixingProxy) myRegistryMixingProxy.lookup("MixingProxy");

        infectedTokens = new ArrayList<>();
        pseudonymList = new ArrayList<>();
        capsules = new ArrayList<>();
        uninformedInfected = new ArrayList<>();

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

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("timer werkt!");
                try {
                    getCapsules();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 100, 1000*60*30);


        frame.setSize(300,300);
        b.setBounds(135,145,30,10);
        panel.add(b);
        frame.add( panel );
        frame.setVisible(true);
    }

    public void uploadFileToMatchingServer(byte[] mydata,  byte[] signature, PublicKey publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        this.signatureDoctor = signature;
        this.publicKeyDoctor = publicKey;
        try {
            File serverpathfile = new File("logMatchingService.txt");
            FileOutputStream out=new FileOutputStream(serverpathfile);
            byte [] data=mydata;
            out.write(data);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                String[] split= lijn.split(" ");
                LocalTime begin = LocalTime.parse(split[1]);
                LocalTime eind = LocalTime.parse(split[2]);
                String hash = split[3];
                int randomnummer = Integer.parseInt(split[4]);
                System.out.println(begin + " " + eind + " " +hash + " "+ randomnummer);
                infectedTokens.add(new usedToken(begin, eind, hash, randomnummer));
            }

            pseudonymList = registrar.getPseudonyms(); //moet nog worden aangepast zodat er enkel pseudonyms van 1 dag worden opgehaald


            if(capsules.size()!=0) {
                for (usedToken token : infectedTokens) {
                    ArrayList<Capsule> temp = capsules;
                    Iterator<Capsule> iterator = temp.iterator();
                    while (iterator.hasNext()) {
                        Capsule capsule = iterator.next();
                        if (Objects.equals(token.getHash(), capsule.getHash())) {
                            if (checkTimeInterval(token.getBeginTijd(), token.getEindTijd(), capsule.getLocalTime())) {
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

    public void getCapsules() throws RemoteException {
        ArrayList<Capsule> lijstCapsules = mixingProxy.getCapsules();
        System.out.println("functie");
        for(Capsule c : lijstCapsules){
            capsules.add(c);
        }
    }

    public byte[] makeHash(byte[] pseudonym, int random) throws NoSuchAlgorithmException {

        String data = pseudonym + "," +  random;
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        return md.digest(data.getBytes(StandardCharsets.UTF_8));
    }

    public boolean checkTimeInterval(LocalTime begin, LocalTime eind, LocalTime user) {
        boolean inInterval = false;
        if(begin.compareTo(user) <= 0 & eind.compareTo(user) >= 0){
            inInterval = true;
        }
        return inInterval;
    }
}
