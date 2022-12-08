import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MatchingService_implementation extends UnicastRemoteObject implements MatchingService{
    Registry myRegistryRegistrar;
    Registry myRegistryMixingProxy;
    Registrar registrar;
    MixingProxy mixingProxy;

    public MatchingService_implementation() throws RemoteException, NotBoundException {
        myRegistryRegistrar = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistryRegistrar.lookup("Registrar");
        myRegistryMixingProxy = LocateRegistry.getRegistry("localhost", 9000, new SslRMIClientSocketFactory());
        mixingProxy = (MixingProxy) myRegistryMixingProxy.lookup("MixingProxy");
    }


}
