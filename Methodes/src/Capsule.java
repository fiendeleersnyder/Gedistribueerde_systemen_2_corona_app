import java.io.Serializable;
import java.time.LocalDateTime;

public class Capsule implements Serializable {
    private LocalDateTime localTime;
    private Token token;
    private String hash;

    public Capsule(LocalDateTime localTime, Token token, String hash) {
        this.localTime = localTime;
        this.token = token;
        this.hash = hash;
    }

    public Capsule(Token token, String hash) {
        this.localTime = null;
        this.token = token;
        this.hash = hash;
    }

    public LocalDateTime getLocalDateTime() {
        return localTime;
    }

    public void setLocalDateTime(LocalDateTime localTime) {
        this.localTime = localTime;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
