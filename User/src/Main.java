import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;

public class Main {
    Registry myRegistry;
    Registrar registrar;
    JFrame frame = new JFrame("Corona-app");
    JTextArea messageArea = new JTextArea(16, 50);

    public Main() throws RemoteException, NotBoundException {
        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistry.lookup("Registrar");

        messageArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

    }

    public String enrollment_phase() {
        String name = JOptionPane.showInputDialog(frame, "What is your name:", "Enrollment phase",
                JOptionPane.PLAIN_MESSAGE);
        String phone_number = JOptionPane.showInputDialog(frame, "What is your phone number:", "Enrollment phase",
                JOptionPane.PLAIN_MESSAGE);
        String result = name + "," + phone_number;
        return result;
    }

    public ArrayList<String> get_tokens(String phone_number) throws RemoteException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, SignatureException {
        return registrar.get_tokens(phone_number);
    }

    public static void main(String args[]) throws NotBoundException, RemoteException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, SignatureException {
        Main main = new Main();
        String name= "Fien De Leersnyder";
        String phone_number = "0471283868";

        /*String[] result = main.enrollment_phase().split(",");
        name = result[0];
        phone_number = result[1];*/

        ArrayList<String> tokens = main.get_tokens(phone_number);

        for(String token: tokens) { //vreemde tokens, is dit het???
            System.out.println(token);
        }


    }
}
