import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
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
import java.util.HashMap;
import java.util.Map;

public class Main {
    Registry myRegistry;
    Registry mixingRegistry;
    Registrar registrar;
    MixingProxy mixingProxy;
    Doctor dokter;
    JFrame frame = new JFrame("Corona-app");
    ArrayList<ArrayList<byte[]>> tokens = new ArrayList<>();
    ArrayList<byte[]> tokensVandaag;
    HashMap<LocalTime, byte[]> tijdTokens = new HashMap<>();
    int aantalBezoeken = 0;
    String name;
    String phone_number;
    String barcode;
    int random_number;
    String CF;
    String hash;
    LocalTime localTime;
    Capsule capsule;

    //user
    public Main() throws RemoteException, NotBoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, SignatureException, InvalidKeyException {
        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistry.lookup("Registrar");
        mixingRegistry = LocateRegistry.getRegistry("localhost", 2019, new SslRMIClientSocketFactory());
        mixingProxy = (MixingProxy) mixingRegistry.lookup("MixingProxy");
        dokter = (Doctor) myRegistry.lookup("Doctor");

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
                tijdTokens.put(localTime, tokensVandaag.get(aantalBezoeken));
                aantalBezoeken++;
                try {
                    mixingProxy.sendCapsule(capsule, phone_number);
                } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
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

        JButton button = new JButton("Send log to doctor");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String dag = LocalDateTime.now().toString();
                    FileWriter fileWriter = new FileWriter("log.txt");
                    BufferedWriter writer = new BufferedWriter(fileWriter);
                    writer.write(dag + "\n");
                    for(Map.Entry<LocalTime, byte[]> entry: tijdTokens.entrySet()) {
                        //key is de tijd, value is de token
                        writer.write(entry.getKey() + "\n" + entry.getValue() + "\n");
                    }
                    writer.write(hash + "\n");
                    writer.write(random_number);
                    fileWriter.close();
                    writer.flush();
                    writer.close();
                }
                catch  (IOException ex) {
                    System.out.println("Error occurred. Try again.");
                    ex.printStackTrace();
                }

                try{
                    File clientpathfile = new File("log.txt");//hier pathname mogelijks nog aanpassen
                    byte [] mydata=new byte[(int) clientpathfile.length()];
                    FileInputStream in=new FileInputStream(clientpathfile);
                    System.out.println("uploading to doctorserver...");
                    in.read(mydata, 0, mydata.length);
                    dokter.uploadFileToServer(mydata);
                    in.close();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(button);
        frame.add(panel);

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
            System.out.println("tokens was 0");
        }

    }

    public static void main(String args[]) throws NotBoundException, RemoteException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, SignatureException {
        System.setProperty("javax.net.ssl.trustStore","Truststore/truststore.ks");
        System.setProperty("javax.net.ssl.trustStorePassword","truststore");
        Main main = new Main();
        main.start();


    }
}
