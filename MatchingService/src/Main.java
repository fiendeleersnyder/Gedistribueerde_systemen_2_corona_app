import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {

    private void startMatchingService() throws RemoteException, NotBoundException {
        try {
            Registry registry = LocateRegistry.getRegistry(4500);
            registry.bind("MatchingService", new MatchingService_implementation());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("matching service is ready");
    }
    //matching service
    public static void main(String[] args) throws NotBoundException, RemoteException {
        Main main = new Main();
        main.startMatchingService();
    }
}
