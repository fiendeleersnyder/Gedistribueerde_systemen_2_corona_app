import java.rmi.Remote;

public interface Doctor extends Remote {
    void uploadFileToServer(byte[] mydata);
}
