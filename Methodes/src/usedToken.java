import java.io.Serializable;
import java.time.LocalDateTime;

public class usedToken implements Serializable {
    private LocalDateTime beginTijd;
    private LocalDateTime eindTijd;
    private String hash;
    private int randomNumber;
    private boolean informed;

    public usedToken(LocalDateTime beginTijd, String hash, int randomNumber) {
        this.beginTijd = beginTijd;
        this.hash = hash;
        this.randomNumber = randomNumber;
        this.informed = false;
    }

    public usedToken(LocalDateTime beginTijd, LocalDateTime eindTijd, String hash, int randomNumber) {
        this.beginTijd = beginTijd;
        this.eindTijd = eindTijd;
        this.hash = hash;
        this.randomNumber = randomNumber;
        this.informed = false;
    }


    public boolean isInfected() { return informed; }

    public void setInfected(boolean informed) { this.informed = informed; }

    public LocalDateTime getBeginTijd() { return beginTijd; }

    public void setBeginTijd(LocalDateTime beginTijd) { this.beginTijd = beginTijd; }

    public LocalDateTime getEindTijd() { return eindTijd; }

    public void setEindTijd(LocalDateTime eindTijd) { this.eindTijd = eindTijd; }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }
}
