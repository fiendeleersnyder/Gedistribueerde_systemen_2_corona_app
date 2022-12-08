import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Registrar extends Remote {
    ArrayList<byte[]> getPseudonyms() throws RemoteException;
}
