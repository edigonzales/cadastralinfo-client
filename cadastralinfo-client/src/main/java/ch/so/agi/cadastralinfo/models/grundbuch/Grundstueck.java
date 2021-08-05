package ch.so.agi.cadastralinfo.models.grundbuch;

public class Grundstueck {
    String gemeinde = "";
    String bfsnr = "";
    String nummerLang = "";
    String nummerKurz = "";
    String grundstuecksart = "";
    String fuehrungsart = "";
    String kantonaleUnterartStichwort = "";
    String kantonaleUnterartStichwortZusatz = "";
    String flaeche = "";
    String plannr = "";
    String anmerkungAv = "";
    boolean isHauptGrundstueck;
    
    public String getGemeinde() {
        return gemeinde;
    }
    public void setGemeinde(String gemeinde) {
        this.gemeinde = gemeinde;
    }
    public String getBfsnr() {
        return bfsnr;
    }
    public void setBfsnr(String bfsnr) {
        this.bfsnr = bfsnr;
    }
    public String getNummerLang() {
        return nummerLang;
    }
    public void setNummerLang(String nummerLang) {
        this.nummerLang = nummerLang;
    }
    public String getNummerKurz() {
        return nummerKurz;
    }
    public void setNummerKurz(String nummerKurz) {
        this.nummerKurz = nummerKurz;
    }
    public String getGrundstuecksart() {
        return grundstuecksart;
    }
    public void setGrundstuecksart(String grundstuecksart) {
        this.grundstuecksart = grundstuecksart;
    }
    public String getFuehrungsart() {
        return fuehrungsart;
    }
    public void setFuehrungsart(String fuehrungsart) {
        this.fuehrungsart = fuehrungsart;
    }
    public String getKantonaleUnterartStichwort() {
        return kantonaleUnterartStichwort;
    }
    public void setKantonaleUnterartStichwort(String kantonaleUnterartStichwort) {
        this.kantonaleUnterartStichwort = kantonaleUnterartStichwort;
    }
    public String getKantonaleUnterartStichwortZusatz() {
        return kantonaleUnterartStichwortZusatz;
    }
    public void setKantonaleUnterartStichwortZusatz(String kantonaleUnterartStichwortZusatz) {
        this.kantonaleUnterartStichwortZusatz = kantonaleUnterartStichwortZusatz;
    }
    public String getFlaeche() {
        return flaeche;
    }
    public void setFlaeche(String flaeche) {
        this.flaeche = flaeche;
    }
    public String getPlannr() {
        return plannr;
    }
    public void setPlannr(String plannr) {
        this.plannr = plannr;
    }
    public String getAnmerkungAv() {
        return anmerkungAv;
    }
    public void setAnmerkungAv(String anmerkungAv) {
        this.anmerkungAv = anmerkungAv;
    }
    public boolean isHauptGrundstueck() {
        return isHauptGrundstueck;
    }
    public void setHauptGrundstueck(boolean isHauptGrundstueck) {
        this.isHauptGrundstueck = isHauptGrundstueck;
    }

}
