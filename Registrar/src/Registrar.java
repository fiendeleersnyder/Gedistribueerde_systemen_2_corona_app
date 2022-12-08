import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.*;
import java.util.ArrayList;

public interface Registrar extends Remote {
    String create_pseudonym(String name, String location) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
    ArrayList<ArrayList<Token>>get_tokens(String phone_number) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException;
    boolean checkValidity(Token token) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException, SignatureException;
    ArrayList<byte[]> getPseudonyms() throws RemoteException;
}
