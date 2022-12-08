import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;

public interface MatchingService extends Remote {
    void uploadFileToMatchingServer(byte[] mydata,  byte[] signature, PublicKey publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException;
    void sendCapsules(ArrayList<Capsule> capsules) throws RemoteException;
}

