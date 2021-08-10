package ch.so.agi.cadastralinfo.models.grundbuch;

public class EigentumAnteil extends Recht {
    private String berechtige;
    private String anteilZaehler;
    private String anteilNenner;
    private String subjektivDinglich;
    private String eigentumsform;
    private String anteilInProsa;
    
    public String getBerechtige() {
        return berechtige;
    }
    public void setBerechtige(String berechtige) {
        this.berechtige = berechtige;
    }
    public String getAnteilZaehler() {
        return anteilZaehler;
    }
    public void setAnteilZaehler(String anteilZaehler) {
        this.anteilZaehler = anteilZaehler;
    }
    public String getAnteilNenner() {
        return anteilNenner;
    }
    public void setAnteilNenner(String anteilNenner) {
        this.anteilNenner = anteilNenner;
    }
    public String getSubjektivDinglich() {
        return subjektivDinglich;
    }
    public void setSubjektivDinglich(String subjektivDinglich) {
        this.subjektivDinglich = subjektivDinglich;
    }
    public String getEigentumsform() {
        return eigentumsform;
    }
    public void setEigentumsform(String eigentumsform) {
        this.eigentumsform = eigentumsform;
    }
    public String getAnteilInProsa() {
        return anteilInProsa;
    }
    public void setAnteilInProsa(String anteilInProsa) {
        this.anteilInProsa = anteilInProsa;
    }
}
