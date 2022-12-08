import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public interface Doctor extends Remote {
    void uploadFileToServer(byte[] mydata) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException; //dient om file van patiënt te krijgen
}
