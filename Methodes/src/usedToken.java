import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class usedToken implements Serializable {
    private LocalTime timeInterval;
    private byte[] hash;
    private int randomNumber;
    private boolean infected;

    public usedToken(LocalTime time, byte[] hash, int randomNumber) {
        this.timeInterval = time;
        this.hash = hash;
        this.randomNumber = randomNumber;
        this.infected = false;
    }

    public usedToken(LocalTime beginTijd, LocalTime eindTijd, byte[] hash, int randomNumber) {
        this.beginTijd = beginTijd;
        this.eindTijd = eindTijd;
        this.hash = hash;
        this.randomNumber = randomNumber;
        this.infected = false;
    }


    public boolean isInfected() { return infected; }

    public void setInfected(boolean infected) { this.infected = infected; }

    public LocalTime getBeginTijd() { return beginTijd; }

    public void setBeginTijd(LocalTime beginTijd) { this.beginTijd = beginTijd; }

    public LocalTime getEindTijd() { return eindTijd; }

    public void setEindTijd(LocalTime eindTijd) { this.eindTijd = eindTijd; }

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
