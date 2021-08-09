package ch.so.agi.cadastralinfo.models.grundbuch;

public class Adresse {
    private String strasse;
    private String hausnummer;
    private String plz;
    private String ort;
    private String land;
    private String rolle;
    
    public String getStrasse() {
        return strasse;
    }
    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }
    public String getHausnummer() {
        return hausnummer;
    }
    public void setHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
    }
    public String getPlz() {
        return plz;
    }
    public void setPlz(String plz) {
        this.plz = plz;
    }
    public String getOrt() {
        return ort;
    }
    public void setOrt(String ort) {
        this.ort = ort;
    }
    public String getLand() {
        return land;
    }
    public void setLand(String land) {
        this.land = land;
    }
    public String getRolle() {
        return rolle;
    }
    public void setRolle(String rolle) {
        this.rolle = rolle;
    }
}
