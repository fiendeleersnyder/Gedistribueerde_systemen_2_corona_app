import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {

    private void startMatchingService() throws RemoteException, NotBoundException {
        try {
            Registry registry = LocateRegistry.getRegistry(4500);
            registry.bind("MatchingService", new MatchingService_implementation());
            //matchingservice moet ook binden met de mixingproxy
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("matching service is ready");
    }
    //matching service
    public static void main(String[] args) throws NotBoundException, RemoteException {
        System.setProperty("javax.net.ssl.trustStore","truststore.ks");
        System.setProperty("javax.net.ssl.trustStorePassword","keystore");
        Main main = new Main();
        main.startMatchingService();
    }
}
