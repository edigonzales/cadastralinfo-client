package ch.so.agi.cadastralinfo.models.grundbuch;

public class NatuerlichePerson extends Person {
    private String name;
    private String vorname;
    private String geburtsjahr;
    private String geburtsmonat;
    private String geburtstag;
    private String geschlecht;
    private String heimatort;
    private String staatsangehoerigkeit;
    private Adresse adresse;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getVorname() {
        return vorname;
    }
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }
    public String getGeburtsjahr() {
        return geburtsjahr;
    }
    public void setGeburtsjahr(String geburtsjahr) {
        this.geburtsjahr = geburtsjahr;
    }
    public String getGeburtsmonat() {
        return geburtsmonat;
    }
    public void setGeburtsmonat(String geburtsmonat) {
        this.geburtsmonat = geburtsmonat;
    }
    public String getGeburtstag() {
        return geburtstag;
    }
    public void setGeburtstag(String geburtstag) {
        this.geburtstag = geburtstag;
    }
    public String getGeschlecht() {
        return geschlecht;
    }
    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }
    public String getHeimatort() {
        return heimatort;
    }
    public void setHeimatort(String heimatort) {
        this.heimatort = heimatort;
    }
    public String getStaatsangehoerigkeit() {
        return staatsangehoerigkeit;
    }
    public void setStaatsangehoerigkeit(String staatsangehoerigkeit) {
        this.staatsangehoerigkeit = staatsangehoerigkeit;
    }
    public Adresse getAdresse() {
        return adresse;
    }
    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }
}
