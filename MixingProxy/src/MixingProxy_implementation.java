import javax.swing.*;
import java.awt.*;
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
import java.util.Collections;

public class MixingProxy_implementation extends UnicastRemoteObject implements MixingProxy{
    Registry myRegistry;
    Registrar registrar;
    PrivateKey privateKey;
    ArrayList<Token> usedTokens;
    ArrayList<Capsule> capsules;
    JFrame frame;
    JLabel text;
    JPanel p;
    ArrayList<JLabel> labels;

    public MixingProxy_implementation() throws IOException, NotBoundException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        usedTokens = new ArrayList<>();
        capsules = new ArrayList<>();
        labels = new ArrayList<>();

        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistry.lookup("Registrar");
        frame= new JFrame("Mixing database");
        text = new JLabel("Mixing database: ");
        p = new JPanel();


        KeyStore keyStore = KeyStore.getInstance("JKS");
        String fileName = "keystore";
        FileInputStream fis = new FileInputStream(fileName);
        keyStore.load(fis,"keystore".toCharArray());
        fis.close();

        privateKey = (PrivateKey) keyStore.getKey("mixingproxy","keystore".toCharArray());
        frame.setSize(500,600);
        p.add(text);
        p.setSize(new Dimension(300,600));
        p.setBackground(new Color(235, 52, 183));
        frame.add(p);
        frame.setVisible(true);

    }
    public void updateGUI() {
        if (!capsules.isEmpty()) {
            JLabel capsule = new JLabel();
            capsule.setText("LocalTime: " + capsules.get(capsules.size() - 1).getLocalTime() + " Token: " + capsules.get(capsules.size() - 1).getToken() + " Hash: " + capsules.get(capsules.size() - 1).getHash());
            p.add(capsule);
            labels.add(capsule);
            frame.setVisible(true);
        }

    }


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
            updateGUI();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(capsule.getHash().getBytes(StandardCharsets.UTF_8));
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
        for (JLabel label: labels) {
            label.setText("");
        }
        return temp;
    }
}
