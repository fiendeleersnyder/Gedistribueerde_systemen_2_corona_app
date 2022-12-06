import java.time.LocalTime;

public class Capsule {
    private LocalTime localTime;
    private byte[] token;
    private byte[] hash;

    public Capsule(Capsule capsule) {
        this.localTime = capsule.localTime;
        this.token = capsule.token;
        this.hash = capsule.hash;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public byte[] getToken() {
        return token;
    }

    public byte[] getHash() {
        return hash;
    }
}
