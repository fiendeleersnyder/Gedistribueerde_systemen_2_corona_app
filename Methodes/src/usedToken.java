import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class usedToken implements Serializable {
    private LocalTime timeInterval;
    private byte[] hash;
    private int randomNumber;

    public usedToken(LocalTime time, byte[] hash, int randomNumber) {
        this.timeInterval = time;
        this.hash = hash;
        this.randomNumber = randomNumber;
    }
    public LocalTime getTimeInterval() { return timeInterval; }

    public void setInterval(LocalTime timeInterval) { this.timeInterval = timeInterval; }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }
}
