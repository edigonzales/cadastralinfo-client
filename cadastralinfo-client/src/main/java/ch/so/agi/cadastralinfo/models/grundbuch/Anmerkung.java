package ch.so.agi.cadastralinfo.models.grundbuch;

import java.util.Date;
import java.util.List;

public class Anmerkung {
    private String tagebuchNummer;
    private Date tagebuchDatumZeit;
    private String alteNummer;
    private String artStichwort;
    private String artZusatz;
    private Date ablaufdatum;
    private List<BeteiligtesGrundstueck> beteiligte;
    private List<String> berechtigte;
    private List<String> belastete;
    
    public String getArtStichwort() {
        return artStichwort;
    }
    public void setArtStichwort(String artStichwort) {
        this.artStichwort = artStichwort;
    }
    public String getArtZusatz() {
        return artZusatz;
    }
    public void setArtZusatz(String artZusatz) {
        this.artZusatz = artZusatz;
    }
    public Date getAblaufdatum() {
        return ablaufdatum;
    }
    public void setAblaufdatum(Date ablaufdatum) {
        this.ablaufdatum = ablaufdatum;
    }
    public List<BeteiligtesGrundstueck> getBeteiligte() {
        return beteiligte;
    }
    public void setBeteiligte(List<BeteiligtesGrundstueck> beteiligte) {
        this.beteiligte = beteiligte;
    }
    public List<String> getBerechtigte() {
        return berechtigte;
    }
    public void setBerechtigte(List<String> berechtigte) {
        this.berechtigte = berechtigte;
    }
    public List<String> getBelastete() {
        return belastete;
    }
    public void setBelastete(List<String> belastete) {
        this.belastete = belastete;
    }
    public String getTagebuchNummer() {
        return tagebuchNummer;
    }
    public void setTagebuchNummer(String tagebuchNummer) {
        this.tagebuchNummer = tagebuchNummer;
    }
    public String getAlteNummer() {
        return alteNummer;
    }
    public void setAlteNummer(String alteNummer) {
        this.alteNummer = alteNummer;
    }
    public Date getTagebuchDatumZeit() {
        return tagebuchDatumZeit;
    }
    public void setTagebuchDatumZeit(Date tagebuchDatumZeit) {
        this.tagebuchDatumZeit = tagebuchDatumZeit;
    }

}
