import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {

    private void startMixingProxy() {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            Registry registry = LocateRegistry.createRegistry(9000, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());

            registry.bind("MixingProxy", new MixingProxy_implementation());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("system is ready");

    }

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.keyStore","keystore");
        System.setProperty("javax.net.ssl.keyStorePassword","keystore");
        Main main = new Main();
        main.startMixingProxy();
    }
}
