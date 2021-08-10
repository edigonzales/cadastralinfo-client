package ch.so.agi.cadastralinfo.models.grundbuch;

public class JuristischePerson extends Person {
    private String nameFirma;
    private String sitz;
    private String firmennummer;
    private String uid;
    
    public String getNameFirma() {
        return nameFirma;
    }
    public void setNameFirma(String nameFirma) {
        this.nameFirma = nameFirma;
    }
    public String getSitz() {
        return sitz;
    }
    public void setSitz(String sitz) {
        this.sitz = sitz;
    }
    public String getFirmennummer() {
        return firmennummer;
    }
    public void setFirmennummer(String firmennummer) {
        this.firmennummer = firmennummer;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }  
}
