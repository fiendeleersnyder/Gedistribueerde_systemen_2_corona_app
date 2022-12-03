import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Main {
    Registry myRegistry;
    Registrar registrar;

    public Main() throws RemoteException, NotBoundException {
        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistry.lookup("Registrar");
    }

    public String get_pseudonym(String name, String location) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String pseudonym = registrar.create_pseudonym(name, location);
        return pseudonym;
    }

    public BitMatrix generate_qrcode(String name, String pseudonym) throws NoSuchAlgorithmException, UnsupportedEncodingException, WriterException {
        Random random = new Random();
        int number = random.nextInt();
        String data = pseudonym + "," +  number;
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
        String result_string = number + "," + name + "," + new String(hash);
        System.out.println(result_string);
        byte[] result = result_string.getBytes(StandardCharsets.UTF_8);
        return new MultiFormatWriter().encode(new String(result, StandardCharsets.UTF_8), BarcodeFormat.QR_CODE, 25, 25);
    }

    public static void main(String args[]) throws NotBoundException, RemoteException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException, WriterException {
        Main main = new Main();
        int business_number = 164951;
        String name = "fried chicken";
        String location = "gent";
        String phone_number = "0471648279";

        /*Scanner in = new Scanner(System.in);

        System.out.println("What is the name of your catering facility? ");
        name = in.nextLine();

        System.out.println("What is the location of your catering facility? ");
        location = in.nextLine();

        System.out.println("What is the business number of your catering facility? ");
        business_number = Integer.parseInt(in.nextLine());

        System.out.println("What is the phone number of your catering facility? ");
        phone_number = in.nextLine();*/

        String pseudonym = main.get_pseudonym(name, location);
        System.out.println(pseudonym);

        BitMatrix qrcode = main.generate_qrcode(name, pseudonym);
        System.out.println(qrcode);

    }
}
