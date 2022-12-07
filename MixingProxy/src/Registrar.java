import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public interface Registrar {
    PublicKey getPublicKey(String phone_number) throws NoSuchAlgorithmException, RemoteException;
}
