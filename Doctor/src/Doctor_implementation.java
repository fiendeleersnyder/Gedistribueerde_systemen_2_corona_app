import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.cert.CertificateException;

public class Doctor_implementation extends UnicastRemoteObject implements Doctor{
    Registry myRegistry;
    MatchingService matchingService;
    PrivateKey privateKey;

    public Doctor_implementation() throws IOException, NotBoundException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        matchingService = (MatchingService) myRegistry.lookup("MatchingService"); //mogelijks moet hier de matchingservice komen die verbonden is met de mixing proxy

        /*KeyStore keyStore = KeyStore.getInstance("JKS");
        String fileName = "Keystore/keystore.jks";
        FileInputStream fis = new FileInputStream(fileName);
        char[] password = "keystore".toCharArray();
        keyStore.load(fis,password);
        fis.close();

        privateKey = (PrivateKey) keyStore.getKey("doctor", "doctor".toCharArray());
*/
    }

    public void uploadFileToServer(byte[] mydata) {
        try {
            File serverpathfile = new File("logDokter.txt");
            FileOutputStream out=new FileOutputStream(serverpathfile);
            byte [] data=mydata;
            out.write(data);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Doctor is done writing data...");
    }


}