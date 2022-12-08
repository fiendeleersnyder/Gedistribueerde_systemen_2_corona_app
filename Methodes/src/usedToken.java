import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class usedToken implements Serializable {
    private LocalTime beginTijd;
    private LocalTime eindTijd;
    private String hash;
    private int randomNumber;
    private boolean informed;

    public usedToken(LocalTime beginTijd, String hash, int randomNumber) {
        this.beginTijd = beginTijd;
        this.hash = hash;
        this.randomNumber = randomNumber;
        this.informed = false;
    }

    public usedToken(LocalTime beginTijd, LocalTime eindTijd, String hash, int randomNumber) {
        this.beginTijd = beginTijd;
        this.eindTijd = eindTijd;
        this.hash = hash;
        this.randomNumber = randomNumber;
        this.informed = false;
    }


    public boolean isInfected() { return informed; }

    public void setInfected(boolean informed) { this.informed = informed; }

    public LocalTime getBeginTijd() { return beginTijd; }

    public void setBeginTijd(LocalTime beginTijd) { this.beginTijd = beginTijd; }

    public LocalTime getEindTijd() { return eindTijd; }

    public void setEindTijd(LocalTime eindTijd) { this.eindTijd = eindTijd; }

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
