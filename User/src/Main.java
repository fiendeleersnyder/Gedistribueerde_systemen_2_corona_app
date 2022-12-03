import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {
    Registry myRegistry;
    Registrar registrar;
    JFrame frame = new JFrame("Corona-app");
    ArrayList<ArrayList<String>> tokens = new ArrayList<>();
    ArrayList<String> tokensVandaag = new ArrayList<>();
    String name;
    String phone_number;
    String barcode;
    int random_number;
    String CF;
    String hash;

    public Main() throws RemoteException, NotBoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, SignatureException, InvalidKeyException {
        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistry.lookup("Registrar");

        JLabel text = new JLabel();
        text.setText("Scan QR-code: ");

        JTextArea barcodeField = new JTextArea(10, 20);

        JButton b = new JButton("submit");
        b.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                barcode = barcodeField.getText();
                random_number = Integer.parseInt(barcode.split(",")[0]);
                CF = barcode.split(",")[1];
                hash = barcode.split(",")[2];
            }
        });

        JPanel p = new JPanel();
        p.add(text);
        p.add(barcodeField);
        p.add(b);
        p.setSize(new Dimension(300,600));
        frame.add(p);

        frame.setSize(300,600);
        frame.pack();
        frame.show();
        frame.setVisible(true);

        name= "Fien De Leersnyder";
        phone_number = "0471283868";
        //enrollment_phase();

        int dag = LocalDateTime.now().getDayOfMonth();

        if (dag == 1 || tokens.isEmpty()) {
            tokens = registrar.get_tokens(phone_number);
        }
        tokensVandaag = tokens.get(dag);



    }

    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
        if (s.equals("submit")) {
            // set the text of the label to the text of the field

        }
    }

    public void enrollment_phase() {
        name = JOptionPane.showInputDialog(frame, "What is your name:", "Enrollment phase",
                JOptionPane.PLAIN_MESSAGE);
        phone_number = JOptionPane.showInputDialog(frame, "What is your phone number:", "Enrollment phase",
                JOptionPane.PLAIN_MESSAGE);
    }

    public void start() {
        if(tokens.size() != 0) {
            for (ArrayList<String> lijst: tokens) {
                for (String token: lijst) {
                    System.out.println(token); //vreemde tokens, is dit het???
                }
            }
        }
        else {
            System.out.println("tokens was 0");
        }

    }

    public static void main(String args[]) throws NotBoundException, RemoteException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, SignatureException {
        Main main = new Main();
        main.start();


    }
}
