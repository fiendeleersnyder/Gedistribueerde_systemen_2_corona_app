import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Doctor extends Remote {
    void uploadFileToServer(byte[] mydata) throws RemoteException; //dient om file van patiënt te krijgen
}
