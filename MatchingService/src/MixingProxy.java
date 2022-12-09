import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface MixingProxy extends Remote {
    ArrayList<Capsule> getCapsules() throws RemoteException;
    ArrayList<usedToken> getInfectedTokens() throws RemoteException;
}
