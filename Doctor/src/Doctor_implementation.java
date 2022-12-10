import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.ArrayList;

public class Doctor_implementation extends UnicastRemoteObject implements Doctor{
    JFrame frame = new JFrame("Corona-app");
    Registry myRegistry;
    Registry myRegistryMixingProxy;
    MatchingService matchingService;
    PrivateKey privateKey;
    PublicKey publicKey;
    byte[] signature;
    ArrayList<usedToken> gebruikteTokens;

    public Doctor_implementation() throws IOException, NotBoundException, NoSuchAlgorithmException {
        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        myRegistryMixingProxy = LocateRegistry.getRegistry("localhost", 9000, new SslRMIClientSocketFactory());
        matchingService = (MatchingService) myRegistry.lookup("MatchingService");

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair pair = keyPairGen.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();

        JButton button = new JButton("Send log to matching service");
        button.addActionListener(e -> {
            try{
                File clientpathfile = new File("logDokter.txt");
                byte [] mydata=new byte[(int) clientpathfile.length()];
                FileInputStream in=new FileInputStream(clientpathfile);
                System.out.println("uploading to matching service...");
                in.read(mydata, 0, mydata.length);
                matchingService.uploadFileToMatchingServer(mydata, gebruikteTokens, signature, publicKey);
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        JPanel p = new JPanel();
        p.add(button);
        p.setSize(new Dimension(300,600));
        frame.add(p);
        frame.setSize(600,600);
        frame.pack();
        frame.setVisible(true);

    }

    public void uploadFileToServer(byte[] mydata, ArrayList<usedToken> gebruikteTokens) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        try {
            File serverpathfile = new File("logDokter.txt");
            FileOutputStream out=new FileOutputStream(serverpathfile);
            out.write(mydata);
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Doctor is done writing data...");
        this.gebruikteTokens = gebruikteTokens;

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        //file omzetten naar een byte array zodat deze gesigned kan worden
        File clientpathfile = new File("logDokter.txt");
        byte [] fileToSign=new byte[(int) clientpathfile.length()];
        FileInputStream in=new FileInputStream(clientpathfile);
        in.read(fileToSign, 0, fileToSign.length);
        in.close();

        signature.update(fileToSign);
        this.signature = signature.sign();
    }


}