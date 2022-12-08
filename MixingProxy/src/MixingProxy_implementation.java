import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.ArrayList;

public class MixingProxy_implementation extends UnicastRemoteObject implements MixingProxy{
    ArrayList<byte[]> usedTokens;
    ArrayList<Capsule> capsules;
    Certificate certificateRegistrar;

    public MixingProxy_implementation() throws IOException {
        usedTokens = new ArrayList<>();
        capsules = new ArrayList<>();
    }

    @Override
    public byte[] sendCapsule(Capsule capsule, String phone_number) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        capsules.add(capsule);
        boolean alreadyUsed = false;
        if (usedTokens.size() != 0) {
            for (byte[] token : usedTokens) {
                if (token == capsule.getToken()) {
                    alreadyUsed = true;
                }
            }
        }
        if (alreadyUsed) {
            return capsule.getHash();
        }
        else {
            usedTokens.add(capsule.getToken());
        }
        Cipher cipher = Cipher.getInstance("RSA");
        //cipher.init(Cipher.DECRYPT_MODE, certificateRegistrar.getPublicKey());
        byte[] decryptedMessageHash = cipher.doFinal(capsule.getToken());
        return decryptedMessageHash;

    }
}
