package ch.so.agi.cadastralinfo.models.grundbuch;

// TODO: sorry, ist natürlich Kappes...
// Überlegen, ob man vom "Recht" ableiten kann.
public class Dienstbarkeit extends Anmerkung {
    private boolean istVerselbstaendigt = false;

    public boolean isIstVerselbstaendigt() {
        return istVerselbstaendigt;
    }

    public void setIstVerselbstaendigt(boolean istVerselbstaendigt) {
        this.istVerselbstaendigt = istVerselbstaendigt;
    }

}
