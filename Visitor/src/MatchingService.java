import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface MatchingService extends Remote {
    ArrayList<Capsule> getInfectedList() throws RemoteException;

}
