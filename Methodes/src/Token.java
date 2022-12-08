import java.io.Serializable;

public class Token implements Serializable {
    private int day;
    private byte[] digitalSignature;
    private int randomNumber;

    public Token(int day, byte[] digitalSignature, int randomNumber) {
        this.day = day;
        this.digitalSignature = digitalSignature;
        this.randomNumber = randomNumber;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public byte[] getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(byte[] digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }
}
