import at.favre.lib.crypto.HKDF;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDateTime;
import java.util.*;


public class Registrar_implementation extends UnicastRemoteObject implements Registrar{
    Key secret_key;
    PrivateKey privateKey;
    PublicKey publicKey;
    Signature signature;
    final int AMOUNT_OF_TOKENS = 50;
    final int DAYS = 31;
    ArrayList<String> phone_numbers;
    Map<String, ArrayList<ArrayList<Token>>> mapping = new HashMap();
    ArrayList<ArrayList<byte[]>> pseudonymen;
    JFrame frame;
    JLabel text;
    JLabel uninformed;
    JPanel p;
    JPanel panel;

    public Registrar_implementation() throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair pair = keyPairGen.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
        frame= new JFrame("Registrar database");
        text = new JLabel("Registrar database: ");
        uninformed = new JLabel("Uninformed users: ");
        p = new JPanel();
        panel = new JPanel();


        int keySize = 128;
        String cipher ="AES"; // gebruiken we AES of iets anders???
        KeyGenerator keyGenerator = KeyGenerator.getInstance(cipher);
        keyGenerator.init(keySize);
        secret_key = keyGenerator.generateKey();

        phone_numbers = new ArrayList<>();
        pseudonymen = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            pseudonymen.add(new ArrayList<>());
        }

        frame.setSize(300,600);
        p.add(text);
        panel.add(uninformed);
        p.setSize(new Dimension(300,300));
        p.setBackground(new Color(255, 111, 0));
        frame.add(p);
        frame.setVisible(true);
    }

    public void updateGUI() {
        JLabel number = new JLabel();
        number.setText(phone_numbers.get(phone_numbers.size()-1));
        p.add(number);
        frame.setVisible(true);

    }

    public void setUninformedUser(String phone_number) {
        JLabel user = new JLabel();
        user.setText(phone_number);
        p.add(user);
        frame.setVisible(true);
    }

    @Override
    public byte[] create_pseudonym(String name, String location) throws RemoteException, NoSuchAlgorithmException {
        int day = LocalDateTime.now().getDayOfMonth();
        String data = name+","+day;
        //secret key voor catering facility
        HKDF hkdf = HKDF.fromHmacSha256();
        byte[] expandedKey = hkdf.expand(secret_key.getEncoded(), data.getBytes(StandardCharsets.UTF_8), 16);
        SecretKey key = new SecretKeySpec(expandedKey, "AES"); //AES-128 key

        //pseudonym
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        String encrypted_string = key.toString();
        String data2 = location + "," + day + "," + encrypted_string;
        byte[] input = data2.getBytes();
        byte[] result = md.digest(input);
        pseudonymen.get(day-1).add(result);
        return result;

    }

    @Override
    public ArrayList<ArrayList<Token>> get_tokens(String phone_number) throws RemoteException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        ArrayList<ArrayList<Token>> tokens = new ArrayList<>();
        /*boolean got_tokens = false;
        for (String number : phone_numbers) {
            if (phone_number.equalsIgnoreCase(number)) {
                got_tokens = true;
            }
        }
        if (got_tokens) {
            return null;
        }*/
        for (int i = 0; i < DAYS; i++) {
            ArrayList<Token> tokensVoorDag = new ArrayList<>(AMOUNT_OF_TOKENS);
            Random random = new Random();
            int number;
            int day = i+1;

            byte[] digitalSignature;
            signature = Signature.getInstance("SHA256withRSA");

            for (int j = 0; j < AMOUNT_OF_TOKENS; j++) {
                signature.initSign(privateKey);
                number = random.nextInt();
                signature.update((number + "," + day).getBytes());
                digitalSignature = signature.sign();
                tokensVoorDag.add(new Token(day, digitalSignature, number));
            }
            tokens.add(tokensVoorDag);
        }
        phone_numbers.add(phone_number);
        mapping.put(phone_number, tokens);
        updateGUI();
        return tokens;
    }

    public boolean checkValidity(Token token) throws InvalidKeyException, SignatureException {
        signature.initVerify(publicKey);
        signature.update((token.getRandomNumber() + "," + token.getDay()).getBytes());
        return signature.verify(token.getDigitalSignature());
    }

    public ArrayList<byte[]> getPseudonyms(int day) throws RemoteException {
        //hier lijst van pseudonumen returnen naar matching service
        return pseudonymen.get(day-1);
    }

    public void sendUninformedUsers(ArrayList<Capsule> uninformedUsers) {
        for (Capsule capsule: uninformedUsers) {
            for (Map.Entry<String,ArrayList<ArrayList<Token>>> entry : mapping.entrySet()){
                for (Token token: entry.getValue().get(capsule.getLocalDateTime().getDayOfMonth()-1)) {
                    if (Objects.equals(capsule.getToken(), token)) {
                        setUninformedUser(entry.getKey());
                    }
                }
            }
        }

    }
}
