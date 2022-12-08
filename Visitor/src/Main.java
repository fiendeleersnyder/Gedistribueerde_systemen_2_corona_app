import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class Main {
    Registry myRegistryRegistrar;
    Registry myRegistryMixingProxy;
    Registrar registrar;
    MixingProxy mixingProxy;
    JFrame frame = new JFrame("Corona-app");
    ArrayList<ArrayList<byte[]>> tokens = new ArrayList<>();
    ArrayList<byte[]> tokensVandaag;
    int aantalBezoeken = 0;
    String name;
    String phone_number;
    String barcode;
    int random_number;
    String CF;
    String hash;
    LocalTime localTime;
    Capsule capsule;


    public Main() throws RemoteException, NotBoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, SignatureException, InvalidKeyException {
        myRegistryRegistrar = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistryRegistrar.lookup("Registrar");
        myRegistryMixingProxy = LocateRegistry.getRegistry("localhost", 9000, new SslRMIClientSocketFactory());
        mixingProxy = (MixingProxy) myRegistryMixingProxy.lookup("MixingProxy");

        JLabel text = new JLabel();
        text.setText("Scan QR-code: ");

        JTextArea barcodeField = new JTextArea(10, 20);

        JButton b = new JButton("submit");
        b.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                barcode = barcodeField.getText();
                capsule = new Capsule(LocalDateTime.now().toLocalTime(), tokensVandaag.get(aantalBezoeken), barcode.split(",")[2].getBytes(StandardCharsets.UTF_8));
                random_number = Integer.parseInt(barcode.split(",")[0]);
                CF = barcode.split(",")[1];
                hash = barcode.split(",")[2];
                localTime = LocalDateTime.now().toLocalTime();
                aantalBezoeken++;
                try {
                    byte[] terug = mixingProxy.sendCapsule(capsule, phone_number);
                    System.out.println(terug.toString());
                } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | RemoteException ex) {
                    ex.printStackTrace();
                }
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
        //frame.show();
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

    public void enrollment_phase() {
        name = JOptionPane.showInputDialog(frame, "What is your name:", "Enrollment phase",
                JOptionPane.PLAIN_MESSAGE);
        phone_number = JOptionPane.showInputDialog(frame, "What is your phone number:", "Enrollment phase",
                JOptionPane.PLAIN_MESSAGE);
    }

    public void start() {
        if(tokens.size() != 0) {
            for (ArrayList<byte[]> lijst: tokens) {
                for (byte[] token: lijst) {
                    System.out.println(token); //vreemde tokens, is dit het???
                }
            }
        }
        else {
            System.out.println("geen tokens gevonden");
        }

    }

    public static void main(String args[]) throws NotBoundException, RemoteException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, SignatureException {
        System.setProperty("javax.net.ssl.trustStore","truststore");
        System.setProperty("javax.net.ssl.trustStorePassword","keystore");
        Main main = new Main();
        main.start();


    }
}
