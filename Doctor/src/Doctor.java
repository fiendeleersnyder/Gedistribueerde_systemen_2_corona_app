import java.io.IOException;
import java.rmi.Remote;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;

public interface Doctor extends Remote {
    void uploadFileToServer(byte[] mydata, ArrayList<usedToken> gebruikteTokens) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException; //dient om file van patiënt te krijgen
}
