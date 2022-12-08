import java.io.Serializable;
import java.time.LocalTime;

public class Capsule implements Serializable {
    private LocalTime localTime;
    private byte[] token;
    private byte[] hash;

    public Capsule(LocalTime localTime, byte[] token, byte[] hash) {
        this.localTime = localTime;
        this.token = token;
        this.hash = hash;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public byte[] getToken() {
        return token;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }
}
