import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    /*JFrame frame = new JFrame("Corona-app");
    Registry myRegistry;
    MatchingService matchingService;
    public Main() throws RemoteException, NotBoundException {
        myRegistry = LocateRegistry.getRegistry("localhost", 4500);
        matchingService = (MatchingService) myRegistry.lookup("MatchingService");

        JButton button = new JButton("Send log to matching service");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    File clientpathfile = new File("logDokter.txt");
                    byte [] mydata=new byte[(int) clientpathfile.length()];
                    FileInputStream in=new FileInputStream(clientpathfile);
                    System.out.println("uploading to matching service...");
                    in.read(mydata, 0, mydata.length);
                    matchingService.uploadFileToMatchingServer(mydata);
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel p = new JPanel();
        p.add(button);
        p.setSize(new Dimension(300,600));
        frame.add(p);
        frame.setSize(600,600);
        frame.pack();
        frame.show();
        frame.setVisible(true);


    }*/

    public void start(){
        try {
            Registry registry = LocateRegistry.getRegistry(4500);
            registry.bind("Doctor", new Doctor_implementation());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("doctor is ready");
    }

    public static void main(String[] args) throws NotBoundException, RemoteException {
        System.setProperty("javax.net.ssl.trustStore","truststore");
        System.setProperty("javax.net.ssl.trustStorePassword","keystore");
        Main main = new Main();
        main.start();
    }
}
