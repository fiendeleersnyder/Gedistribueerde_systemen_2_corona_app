import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;

public interface MatchingService extends Remote {
    void uploadFileToMatchingServer(byte[] mydata, ArrayList<usedToken> gebruikteTokens,  byte[] signature, PublicKey publicKey) throws RemoteException;
}
