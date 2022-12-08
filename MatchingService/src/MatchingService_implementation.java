import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;

public class MatchingService_implementation extends UnicastRemoteObject implements MatchingService{
    Registry myRegistryRegistrar;
    Registry myRegistryMixingProxy;
    Registrar registrar;
    MixingProxy mixingProxy;
    PublicKey publicKeyDoctor;
    byte[] signatureDoctor;

    public MatchingService_implementation() throws RemoteException, NotBoundException {
        myRegistryRegistrar = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistryRegistrar.lookup("Registrar");

        myRegistryMixingProxy = LocateRegistry.getRegistry("localhost", 9000, new SslRMIClientSocketFactory());
        mixingProxy = (MixingProxy) myRegistryMixingProxy.lookup("MixingProxy");
    }

    public void uploadFileToMatchingServer(byte[] mydata,  byte[] signature, PublicKey publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        this.signatureDoctor = signature;
        this.publicKeyDoctor = publicKey;
        try {
            File serverpathfile = new File("logMatchingService.txt");
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

        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(publicKeyDoctor);
        //file omzetten naar een byte array zodat deze gesigned kan worden
        File clientpathfile = new File("logMatchingService.txt");
        byte [] fileToSign=new byte[(int) clientpathfile.length()];
        FileInputStream in=new FileInputStream(clientpathfile);
        in.read(fileToSign, 0, fileToSign.length);
        in.close();

        sign.update(fileToSign);
        boolean signed = sign.verify(signatureDoctor);

        if(signed==true){
            System.out.println("Matching Service is done writing data...");
        }
        else System.out.println("An error occurred: the signature provided by the doctor and the signature" +
                " generated by the matching service don't match.");
    }
}
