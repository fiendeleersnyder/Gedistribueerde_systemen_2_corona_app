import at.favre.lib.crypto.HKDF;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;


public class Registrar_implementation extends UnicastRemoteObject implements Registrar{
    Key secret_key;
    final int AMOUNT_OF_TOKENS = 50;
    final int DAYS = 31;
    ArrayList<String> phone_numbers;

    public Registrar_implementation() throws RemoteException, NoSuchAlgorithmException {
        int keySize = 128;
        String cipher ="AES"; // gebruiken we AES of iets anders???
        KeyGenerator keyGenerator = KeyGenerator.getInstance(cipher);
        keyGenerator.init(keySize);
        secret_key = keyGenerator.generateKey();

        phone_numbers = new ArrayList<>();
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
    public ArrayList<ArrayList<String>> get_tokens(String phone_number) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException {
        ArrayList<ArrayList<String>> tokens = new ArrayList<>();
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
            ArrayList<String> tokensVoorDag = new ArrayList<>(AMOUNT_OF_TOKENS);
            Random random = new Random();
            int number;
            LocalDateTime now = LocalDateTime.now();
            int day = now.getDayOfMonth();
            byte[] digitalSignature;
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, secureRandom);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());

            for (int j = 0; j < AMOUNT_OF_TOKENS; j++) {
                number = random.nextInt();
                signature.update(Integer.toString(number + day).getBytes());
                digitalSignature = signature.sign();
                tokensVoorDag.add(new String(digitalSignature));
            }
            tokens.add(tokensVoorDag);
        }
        phone_numbers.add(phone_number);
        return tokens;
    }
}
