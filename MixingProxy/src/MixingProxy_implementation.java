import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MixingProxy_implementation extends UnicastRemoteObject implements MixingProxy{
    Registry myRegistry;
    Registrar registrar;
    ArrayList<Token> usedTokens;
    ArrayList<Capsule> capsules;
    Certificate certificateRegistrar;

    public MixingProxy_implementation() throws IOException, NotBoundException {
        usedTokens = new ArrayList<>();
        capsules = new ArrayList<>();

        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistry.lookup("Registrar");
    }

    @Override
    public byte[] sendCapsule(Capsule capsule, String phone_number) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, RemoteException {
        boolean valid = registrar.checkValidity(capsule.getToken());
        boolean correctDay = false;
        if (capsule.getToken().getDay() == LocalDateTime.now().getDayOfMonth()) {
            correctDay = true;
        }
        boolean alreadyUsed = false;
        if (usedTokens.size() != 0) {
            for (Token token : usedTokens) {
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

        //Cipher cipher = Cipher.getInstance("RSA");
        //cipher.init(Cipher.DECRYPT_MODE, certificateRegistrar.getPublicKey());
        //byte[] decryptedMessageHash = cipher.doFinal(capsule.getToken());
        capsules.add(capsule);
        return new byte[0];

    }
}
