import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class Main {
    Registry myRegistry;
    Registrar registrar;
    int number;

    public Main() throws RemoteException, NotBoundException {
        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistry.lookup("Registrar");
    }

    public byte[] get_pseudonym(String name, String location) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return registrar.create_pseudonym(name, location);
    }

    public BitMatrix generate_qrcode(String name, byte[] pseudonym) throws NoSuchAlgorithmException, WriterException {
        Random random = new Random();
        number = random.nextInt();
        String data = number + "," + Arrays.toString(pseudonym);
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        byte[] bytedate = data.getBytes(StandardCharsets.UTF_8);
        System.out.println(bytedate);
        md.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] hash = md.digest();
        String result_string = number + "," + name + "," + Arrays.toString(hash);
        System.out.println(number + "%" + name + "%" + Arrays.toString(hash));
        byte[] result = result_string.getBytes(StandardCharsets.UTF_8);
        return new MultiFormatWriter().encode(new String(result, StandardCharsets.UTF_8), BarcodeFormat.QR_CODE, 25, 25);
    }

    public static void main(String args[]) throws NotBoundException, IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, WriterException {
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

        byte[] pseudonym = main.get_pseudonym(name, location);
        System.out.println(Arrays.toString(pseudonym));
        System.out.println(pseudonym);

        BitMatrix qrcode = main.generate_qrcode(name, pseudonym);
        System.out.println(qrcode);
    }
}
