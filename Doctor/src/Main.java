import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {

    public void start(){
        try {
            Registry registry = LocateRegistry.getRegistry(4500);
            registry.rebind("Doctor", new Doctor_implementation());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("doctor is ready");
    }

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore","truststore");
        System.setProperty("javax.net.ssl.trustStorePassword","keystore");
        Main main = new Main();
        main.start();
    }
}
