import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {

    private void startMixingProxy(){
        try {
            Registry registry = LocateRegistry.createRegistry(4500);

            registry.rebind("MixingProxy", new MixingProxy_implementation());
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
