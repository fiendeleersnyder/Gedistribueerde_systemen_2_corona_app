import java.io.FileInputStream;
import java.io.IOException;
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
    PrivateKey privateKey;
    ArrayList<Token> usedTokens;
    ArrayList<Capsule> capsules;

    public MixingProxy_implementation() throws IOException, NotBoundException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        usedTokens = new ArrayList<>();
        capsules = new ArrayList<>();

        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistry.lookup("Registrar");

        KeyStore keyStore = KeyStore.getInstance("JKS");
        String fileName = "keystore";
        FileInputStream fis = new FileInputStream(fileName);
        keyStore.load(fis,"keystore".toCharArray());
        fis.close();

        privateKey = (PrivateKey) keyStore.getKey("mixingproxy","keystore".toCharArray());

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
}
