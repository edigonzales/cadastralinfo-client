package ch.so.agi.cadastralinfo.models.grundbuch;

public class BeteiligtesGrundstueck {
    private String egbtbid = "-";
    private String tagebuchNummer = "-";
    private String tagebuchDatumZeit = "-";
    private String ref;
    
    public String getEgbtbid() {
        return egbtbid;
    }
    public void setEgbtbid(String egbtbid) {
        this.egbtbid = egbtbid;
    }
    public String getTagebuchNummer() {
        return tagebuchNummer;
    }
    public void setTagebuchNummer(String tagebuchNummer) {
        this.tagebuchNummer = tagebuchNummer;
    }
    public String getTagebuchDatumZeit() {
        return tagebuchDatumZeit;
    }
    public void setTagebuchDatumZeit(String tagebuchDatumZeit) {
        this.tagebuchDatumZeit = tagebuchDatumZeit;
    }
    public String getRef() {
        return ref;
    }
    public void setRef(String ref) {
        this.ref = ref;
    }
}
