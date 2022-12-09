import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    Registry myRegistryRegistrar;
    Registry myRegistryMixingProxy;
    Registrar registrar;
    MixingProxy mixingProxy;
    MatchingService matchingService;
    java.security.cert.Certificate certMixingProxy;
    Doctor doctor;
    JFrame frame;// = new JFrame("Corona-app");
    ArrayList<ArrayList<Token>> tokens = new ArrayList<>();
    ArrayList<Token> tokensVandaag;
    ArrayList<usedToken> gebruikteTokens;
    int aantalBezoeken = 0;
    String name;
    String phone_number;
    String barcode;
    int random_number;
    String CF;
    String hash;
    LocalDateTime localDateTime;
    Capsule capsule;
    usedToken usedToken;
    boolean aanwezig = false;


    public Main() throws IOException, NotBoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, SignatureException, InvalidKeyException, KeyStoreException, CertificateException {
        myRegistryRegistrar = LocateRegistry.getRegistry("localhost", 4500);
        registrar = (Registrar) myRegistryRegistrar.lookup("Registrar");
        myRegistryMixingProxy = LocateRegistry.getRegistry("localhost", 9000, new SslRMIClientSocketFactory());
        mixingProxy = (MixingProxy) myRegistryMixingProxy.lookup("MixingProxy");
        matchingService = (MatchingService) myRegistryRegistrar.lookup("MatchingService");
        doctor = (Doctor) myRegistryRegistrar.lookup("Doctor");
        frame = new JFrame("Corona-app");
        frame.setSize(new Dimension(1000,600));


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

        java.util.Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("timer werkt!");
                if (aanwezig) {
                    if (LocalDateTime.now().compareTo(gebruikteTokens.get(aantalBezoeken-1).getBeginTijd().plusSeconds(10)) > 0) {
                        try {
                            System.out.println("nieuwe token is verstuurd");
                            capsule = new Capsule(tokensVandaag.get(aantalBezoeken), hash);
                            mixingProxy.sendCapsule(capsule);
                            gebruikteTokens.add(new usedToken(LocalDateTime.now(), hash, random_number));
                            aantalBezoeken++;
                        } catch (RemoteException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, 0, 1000*30);

        JLabel textName = new JLabel();
        textName.setText("Name: " + name);

        JLabel textNumber = new JLabel();
        textNumber.setText("Phone number: " + phone_number);

        JLabel text = new JLabel();
        text.setText("Scan QR-code: ");

        JTextArea barcodeField = new JTextArea(10, 20);

        JButton b = new JButton("submit");
        JButton leave = new JButton("Leave catering facility");
        JButton button = new JButton("Send log to doctor");
        JLabel imageLabel = new JLabel();
        b.addActionListener(e -> {
            barcode = barcodeField.getText();
            barcodeField.setText("");
            hash = barcode.split("%")[2];
            localDateTime = LocalDateTime.now();
            capsule = new Capsule(localDateTime, tokensVandaag.get(aantalBezoeken), hash);
            random_number = Integer.parseInt(barcode.split("%")[0]);
            CF = barcode.split("%")[1];
            try {
                byte[] signedHash = mixingProxy.sendCapsule(capsule);
                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initVerify(certMixingProxy.getPublicKey());
                signature.update(hash.getBytes(StandardCharsets.UTF_8));
                boolean signed = signature.verify(signedHash);
                if (signed) {
                    aantalBezoeken++;
                    System.out.println("Sign oke");
                    usedToken = new usedToken(localDateTime, hash, random_number);
                    gebruikteTokens.add(usedToken);
                    aanwezig = true;
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

        leave.addActionListener(e -> {
            System.out.println("Uit cafe");
            aanwezig = false;
            for(usedToken usedToken: gebruikteTokens) {
                usedToken.setEindTijd(LocalDateTime.now());
            }
        });

        JPanel p = new JPanel();
        p.setSize(new Dimension(300,600));
        p.add(textName);
        p.add(textNumber);
        p.add(text);
        p.add(barcodeField);
        p.add(leave);
        p.add(b);
        p.add(imageLabel);
        p.setBackground(Color.RED);
        frame.add(p);

        button.addActionListener(e -> {
            try{
                FileWriter fileWriter = new FileWriter("log.txt");
                for(usedToken usedToken: gebruikteTokens) {
                    fileWriter.write(usedToken + "%" + usedToken.getBeginTijd() + "%" + usedToken.getEindTijd() + "%" + usedToken.getHash() + "%" + usedToken.getRandomNumber() + "\n");
                }
                fileWriter.close();
            }
            catch  (IOException ex) {
                System.out.println("Error occurred while trying to write the log. Try again.");
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
        panel.setSize(new Dimension(150,600));
        panel.add(button);
        panel.add(infected);
        panel.setBackground(Color.CYAN);
        frame.add(panel);
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
