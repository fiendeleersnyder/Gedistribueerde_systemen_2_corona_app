import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Registrar extends Remote {
    boolean checkValidity(Token token) throws RemoteException;
}
