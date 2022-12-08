import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class Main {
    Registry myRegistryRegistrar;
    Registry myRegistryMixingProxy;
    Registrar registrar;
    MixingProxy mixingProxy;
    java.security.cert.Certificate certMixingProxy;
    Doctor doctor;
    JFrame frame = new JFrame("Corona-app");
    ArrayList<ArrayList<Token>> tokens = new ArrayList<>();
    ArrayList<Token> tokensVandaag;
    ArrayList<usedToken> gebruikteTokens;
    //HashMap<LocalTime, Token> tijdTokens = new HashMap<>();
    int aantalBezoeken = 0;
    String name;
    String phone_number;
    String barcode;
    int random_number;
    String CF;
    byte[] hash;
    LocalTime localTime;
    Capsule capsule;
    usedToken usedToken;


    public Main() throws IOException, NotBoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, SignatureException, InvalidKeyException, KeyStoreException, CertificateException {
        myRegistryRegistrar = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistryRegistrar.lookup("Registrar");
        myRegistryMixingProxy = LocateRegistry.getRegistry("localhost", 9000, new SslRMIClientSocketFactory());
        mixingProxy = (MixingProxy) myRegistryMixingProxy.lookup("MixingProxy");
        doctor = (Doctor) myRegistryRegistrar.lookup("Doctor");

        KeyStore keyStore = KeyStore.getInstance("JKS");
        String fileName = "keystore";
        FileInputStream fis = new FileInputStream(fileName);
        keyStore.load(fis,"keystore".toCharArray());
        fis.close();

        certMixingProxy = keyStore.getCertificate("mixingproxy");

        gebruikteTokens = new ArrayList<>();

        name= "Fien De Leersnyder";
        phone_number = "0471283868";
        //enrollment_phase();

        JLabel textName = new JLabel();
        textName.setText("Name: " + name);

        JLabel textNumber = new JLabel();
        textNumber.setText("Phone number: " + phone_number);

        JLabel text = new JLabel();
        text.setText("Scan QR-code: ");

        JTextArea barcodeField = new JTextArea(10, 20);

        JButton b = new JButton("submit");
        JButton button = new JButton("Send log to doctor");
        JLabel imageLabel = new JLabel();
        b.addActionListener(e -> {
            barcode = barcodeField.getText();
            barcodeField.setText("");
            hash = barcode.split(",")[2].getBytes(StandardCharsets.UTF_8);
            capsule = new Capsule(LocalDateTime.now().toLocalTime(), tokensVandaag.get(aantalBezoeken), hash);
            random_number = Integer.parseInt(barcode.split(",")[0]);
            CF = barcode.split(",")[1];
            localTime = LocalDateTime.now().toLocalTime();
            usedToken = new usedToken(localTime,hash, random_number);
            gebruikteTokens.add(usedToken);
            //tijdTokens.put(localTime, tokensVandaag.get(aantalBezoeken));
            aantalBezoeken++;
            try {
                byte[] signedHash = mixingProxy.sendCapsule(capsule, phone_number);
                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initVerify(certMixingProxy.getPublicKey());
                signature.update(hash);
                boolean signed = signature.verify(signedHash);
                if (signed) {
                    System.out.println("Sign oke");
                    usedToken = new usedToken(localTime,hash, random_number);
                    gebruikteTokens.add(usedToken);
                    //identicon
                    BufferedImage image = Identicon.generateIdenticons(signedHash, 150,150);
                    File imageFile = new File("image.jpg");
                    ImageIO.write(image, "jpg", imageFile);
                    imageLabel.setIcon(new ImageIcon(image));
                    imageLabel.setVisible(true);

                }

            } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | SignatureException | IOException ex) {
                ex.printStackTrace();
            }
        });

        JPanel p = new JPanel();
        p.setSize(new Dimension(300,600));
        p.add(textName);
        p.add(textNumber);
        p.add(text);
        p.add(barcodeField);
        p.add(b);
        p.add(imageLabel);
        frame.add(p);

        button.addActionListener(e -> {
            try{
                String dag = LocalDateTime.now().toString();
                FileWriter fileWriter = new FileWriter("log.txt");
                BufferedWriter writer = new BufferedWriter(fileWriter);
                writer.write(dag + "\n");
                for(usedToken usedToken: gebruikteTokens) {
                    writer.write(usedToken.getTimeInterval() + "\n" + usedToken.getHash() + "\n" + usedToken.getRandomNumber() + "\n");
                }
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
                doctor.uploadFileToServer(mydata);
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        JPanel panel = new JPanel();
        panel.add(button);
        panel.setSize(new Dimension(300,600));
        frame.add(panel);


        frame.setSize(300,600);
        frame.pack();
        frame.setVisible(true);

        int dag = LocalDateTime.now().getDayOfMonth();

        if (dag == 1 || tokens.isEmpty()) {
            tokens = registrar.get_tokens(phone_number);
        }
        tokensVandaag = tokens.get(dag-1);

    }

    public void enrollment_phase() {
        name = JOptionPane.showInputDialog(frame, "What is your name:", "Enrollment phase",
                JOptionPane.PLAIN_MESSAGE);
        phone_number = JOptionPane.showInputDialog(frame, "What is your phone number:", "Enrollment phase",
                JOptionPane.PLAIN_MESSAGE);
    }

    public void start() {
        if(tokens.size() != 0) {
            for (ArrayList<Token> lijst: tokens) {
                for (Token token: lijst) {
                    System.out.println(token.getDay() + "," + token.getRandomNumber());
                }
            }
        }
        else {
            System.out.println("geen tokens gevonden");
        }

    }

    public static void main(String args[]) throws NotBoundException, IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, SignatureException, CertificateException, KeyStoreException {
        System.setProperty("javax.net.ssl.trustStore","truststore");
        System.setProperty("javax.net.ssl.trustStorePassword","keystore");
        Main main = new Main();
        main.start();


    }
}
