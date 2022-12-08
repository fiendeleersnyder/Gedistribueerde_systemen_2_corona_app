import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    private void startMatchingService() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost",4500);
            registry.rebind("MatchingService", new MatchingService_implementation());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("matching service is ready");
    }

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore","truststore");
        System.setProperty("javax.net.ssl.trustStorePassword","keystore");
        Main main = new Main();
        main.startMatchingService();
    }
}