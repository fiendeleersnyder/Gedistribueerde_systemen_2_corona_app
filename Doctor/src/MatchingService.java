import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface MatchingService extends Remote {
    void uploadFileToMatchingServer(byte[] mydata,  byte[] signature, PublicKey publicKey) throws RemoteException;
}
