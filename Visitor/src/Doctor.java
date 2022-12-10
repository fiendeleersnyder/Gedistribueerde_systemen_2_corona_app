import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Doctor extends Remote {
    void uploadFileToServer(byte[] mydata, ArrayList<usedToken> gebruikteTokens) throws RemoteException;
}