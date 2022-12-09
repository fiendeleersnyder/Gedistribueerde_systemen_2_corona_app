import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public interface MixingProxy extends Remote {
    byte[] sendCapsule(Capsule capsule, String phoneNumber) throws RemoteException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException;

    void sendInfectedTokens(ArrayList<usedToken> infectedInformed) throws RemoteException;
}
