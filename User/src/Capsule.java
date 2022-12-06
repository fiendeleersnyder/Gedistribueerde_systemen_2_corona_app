import java.time.LocalTime;

public class Capsule {
    private LocalTime localTime;
    private byte[] token;
    private byte[] hash;

    public Capsule(LocalTime localTime, byte[] token, byte[] hash) {
        this.localTime = localTime;
        this.token = token;
        this.hash = hash;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }
}
