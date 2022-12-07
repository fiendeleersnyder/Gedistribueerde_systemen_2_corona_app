import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MatchingService_implementation extends UnicastRemoteObject implements MatchingService{
    Registry myRegistry;
    Registry mixingRegistry;
    Registrar registrar;
    MixingProxy mixingProxy;

    public MatchingService_implementation() throws RemoteException, NotBoundException {
        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistry.lookup("Registrar");
        mixingRegistry = LocateRegistry.getRegistry("localhost", 2019, new SslRMIClientSocketFactory());
        mixingProxy = (MixingProxy) mixingRegistry.lookup("MixingProxy");
    }


}
