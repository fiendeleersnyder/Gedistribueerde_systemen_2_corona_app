import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// dokter heeft minimale GUI
// moet van patient log.txt krijgen en moet deze signen en vervolgens doorsturen naar MatchingService
// dokter zal dus als een server werken voor patient: zo kan file van patient naar dokter gestuurd worden
// dokter zal als een client werken voor matching service: zo kan file van dokter naar matching service gestuurd worden

public class Main {
    JFrame frame = new JFrame("Corona-app");

    public Main() {
        JButton button = new JButton("Send log to matching service");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //hierin matching service oproepen matchingService.sendFile(logDokter.txt)
            }
        });
        JPanel p = new JPanel();
        p.add(button);
        p.setSize(new Dimension(300,600));
        frame.add(p);
        frame.setSize(300,600);
        frame.pack();
        frame.show();
        frame.setVisible(true);
    }

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
