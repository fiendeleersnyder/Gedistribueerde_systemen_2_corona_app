import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    private void startMatchingService() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost",4500);
            registry.bind("MatchingService", new MatchingService_implementation());

            Registry myRegistryMixingProxy = LocateRegistry.getRegistry("localhost", 9000, new SslRMIClientSocketFactory());
            myRegistryMixingProxy.bind("MatchingService", new MatchingService_implementation());
            //matchingservice moet ook binden met de mixingproxy
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