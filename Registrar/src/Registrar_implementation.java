import at.favre.lib.crypto.HKDF;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Registrar_implementation extends UnicastRemoteObject implements Registrar{
    Key secret_key;
    PrivateKey privateKey;
    final int AMOUNT_OF_TOKENS = 50;
    final int DAYS = 31;
    ArrayList<String> phone_numbers;
    Map<String, ArrayList<ArrayList<byte[]>>> mapping = new HashMap();
    JFrame frame= new JFrame("Content database");
    JLabel text = new JLabel("Content database: ");
    JPanel p = new JPanel();

    public Registrar_implementation() throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        String fileName = "Keystore/keystore.jks";
        FileInputStream fis = new FileInputStream(fileName);
        char[] password = "keystore".toCharArray();
        keyStore.load(fis,password);
        fis.close();

        privateKey = (PrivateKey) keyStore.getKey("registrar", "registrar".toCharArray());

        int keySize = 128;
        String cipher ="AES"; // gebruiken we AES of iets anders???
        KeyGenerator keyGenerator = KeyGenerator.getInstance(cipher);
        keyGenerator.init(keySize);
        secret_key = keyGenerator.generateKey();

        phone_numbers = new ArrayList<>();

        p.add(text);
        p.setSize(new Dimension(300,600));
        frame.add(p);

        frame.setSize(300,600);
        frame.pack();
        frame.show();
        frame.setVisible(true);
    }

    public void updateGUI() {
        JLabel number = new JLabel();
        number.setText(phone_numbers.get(phone_numbers.size()-1));
        p.add(number);
        frame.show();

    }

    //geen idee als dit goed is, momenteel wordt 1 pseudonym gemaakt maar mss handig als het voor een hele maand kan
    @Override
    public String create_pseudonym(String name, String location) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        LocalDateTime now = LocalDateTime.now();
        String data = name+","+now;
        //secret key voor catering facility
        HKDF hkdf = HKDF.fromHmacSha256();
        byte[] expandedKey = hkdf.expand(secret_key.getEncoded(), "aes-key".getBytes(StandardCharsets.UTF_8), 16);
        SecretKey key = new SecretKeySpec(expandedKey, "AES"); //AES-128 key
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(expandedKey));
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        //pseudonym
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        String encrypted_string = new String(encrypted);
        String data2 = location + "," + now + "," + encrypted_string;
        byte[] input = data2.getBytes();
        byte[] result = md.digest(input);
        String pseudonym = new String(result);

        return pseudonym;
    }

    @Override
    public ArrayList<ArrayList<byte[]>> get_tokens(String phone_number) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException {
        ArrayList<ArrayList<byte[]>> tokens = new ArrayList<>();
        boolean got_tokens = false;
        for (String number : phone_numbers) {
            if (phone_number.equalsIgnoreCase(number)) {
                got_tokens = true;
            }
        }
        if (got_tokens) {
            return null;
        }
        for (int i = 0; i < DAYS; i++) {
            ArrayList<byte[]> tokensVoorDag = new ArrayList<>(AMOUNT_OF_TOKENS);
            Random random = new Random();
            int number;
            LocalDateTime now = LocalDateTime.now();
            int day = now.getDayOfMonth();
            byte[] digitalSignature;

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);

            for (int j = 0; j < AMOUNT_OF_TOKENS; j++) {
                number = random.nextInt();
                signature.update((number + "," + day).getBytes());
                digitalSignature = signature.sign();
                tokensVoorDag.add(digitalSignature);
            }
            tokens.add(tokensVoorDag);
        }
        phone_numbers.add(phone_number);
        mapping.put(phone_number, tokens);
        updateGUI();
        return tokens;
    }

}
