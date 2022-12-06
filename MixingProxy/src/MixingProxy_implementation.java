import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;

public class MixingProxy_implementation extends UnicastRemoteObject implements MixingProxy{
    Registry myRegistry;
    Registrar registrar;
    ArrayList<byte[]> usedTokens;

    public MixingProxy_implementation() throws RemoteException, NotBoundException {
        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistry.lookup("Registrar");
    }

    @Override
    public byte[] sendCapsule(Capsule capsule, String phone_number) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        boolean alreadyUsed = false;
        for (byte[] token: usedTokens) {
            if (token == capsule.getToken()) {
                alreadyUsed = true;
            }
        }
        if (alreadyUsed) {
            return capsule.getHash();
        }
        else {
            usedTokens.add(capsule.getToken());
        }
        PublicKey publicKey = registrar.getPublicKey(phone_number);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedMessageHash = cipher.doFinal(capsule.getToken());
        return decryptedMessageHash;

    }
}
