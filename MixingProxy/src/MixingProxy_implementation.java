import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MixingProxy_implementation extends UnicastRemoteObject implements MixingProxy{
    Registry myRegistry;
    Registrar registrar;
    MatchingService matchingService;
    PrivateKey privateKey;
    ArrayList<Token> usedTokens;
    ArrayList<Capsule> capsules;
    JFrame frame;//= new JFrame("Mixing database");
    JLabel text;// = new JLabel("Registrar database: ");
    JPanel p;// = new JPanel();

    public MixingProxy_implementation() throws IOException, NotBoundException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        usedTokens = new ArrayList<>();
        capsules = new ArrayList<>();

        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistry.lookup("Registrar");
        matchingService = (MatchingService) myRegistry.lookup("MatchingService");


        KeyStore keyStore = KeyStore.getInstance("JKS");
        String fileName = "keystore";
        FileInputStream fis = new FileInputStream(fileName);
        keyStore.load(fis,"keystore".toCharArray());
        fis.close();

        privateKey = (PrivateKey) keyStore.getKey("mixingproxy","keystore".toCharArray());

        b.addActionListener(e -> {
            try {
                matchingService.sendCapsules(capsules);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        p.add(text);
        p.setSize(new Dimension(300,600));
        p.setBackground(new Color(235, 52, 183));
        frame.add(p);
        frame.setVisible(true);

    }

    @Override
    public byte[] sendCapsule(Capsule capsule, String phone_number) throws NoSuchAlgorithmException, InvalidKeyException, RemoteException, SignatureException {
        boolean valid = registrar.checkValidity(capsule.getToken());
        boolean correctDay = capsule.getToken().getDay() == LocalDateTime.now().getDayOfMonth();
        boolean alreadyUsed = false;
        if (usedTokens.size() != 0) {
            for (Token token : usedTokens) {
                if (token == capsule.getToken()) {
                    alreadyUsed = true;
                }
            }
        }
        if (alreadyUsed || !valid || !correctDay) {
            return new byte[0];
        }
        else {
            usedTokens.add(capsule.getToken());
            capsules.add(capsule);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(capsule.getHash());
            return signature.sign();
        }

    }

    @Override
    public ArrayList<Capsule> getCapsules() throws RemoteException {
        Collections.shuffle(capsules);
        ArrayList<Capsule> temp = new ArrayList<>();
        for (Capsule capsule: capsules) {
            temp.add(capsule);
        }
        capsules.clear();
        return temp;
    }
}
