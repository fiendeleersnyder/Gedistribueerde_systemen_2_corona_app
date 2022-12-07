import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Main {
    Registrar registrar;
    private void startMixingProxy(){
        try {
            Registry registry = LocateRegistry.createRegistry(2019, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
            //Registry registry = LocateRegistry.createRegistry(9000, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
            registry.bind("MixingProxy", new MixingProxy_implementation());

            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4500);
            registrar = (Registrar) myRegistry.lookup("Registrar");

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("system is ready");
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.startMixingProxy();
    }
}
