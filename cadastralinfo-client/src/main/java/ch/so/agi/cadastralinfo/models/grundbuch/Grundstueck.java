package ch.so.agi.cadastralinfo.models.grundbuch;

import java.util.List;

public class Grundstueck {
    String gemeinde = "-";
    String bfsnr = "-";
    String gbamt = "-";
    String nummer;
    String egrid;
    String grundstuecksart = "-";
    String fuehrungsart = "-";
    String kantonaleUnterartStichwort = "-";
    String kantonaleUnterartZusatz = "-";
    String flaeche = "-";
    String plannr = "-";
    String anmerkungAv = "-";
    boolean isHauptGrundstueck;
    List<HaengigesGeschaeft> haengigeGeschaefte;
    List<AVBemerkung> avBemerkungen;
    MutationsNummer letzteVollzogeneMutation;
    
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
    public String getGbamt() {
        return gbamt;
    }
    public void setGbamt(String gbamt) {
        this.gbamt = gbamt;
    }
    
    public String getNummer() {
        return nummer;
    }
    public void setNummer(String nummer) {
        this.nummer = nummer;
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
    public String getKantonaleUnterartZusatz() {
        return kantonaleUnterartZusatz;
    }
    public void setKantonaleUnterartZusatz(String kantonaleUnterartZusatz) {
        this.kantonaleUnterartZusatz = kantonaleUnterartZusatz;
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
    public String getEgrid() {
        return egrid;
    }
    public void setEgrid(String egrid) {
        this.egrid = egrid;
    }
    public List<HaengigesGeschaeft> getHaengigeGeschaefte() {
        return haengigeGeschaefte;
    }
    public void setHaengigeGeschaefte(List<HaengigesGeschaeft> haengigeGeschaefte) {
        this.haengigeGeschaefte = haengigeGeschaefte;
    }
    public MutationsNummer getLetzteVollzogeneMutation() {
        return letzteVollzogeneMutation;
    }
    public void setLetzteVollzogeneMutation(MutationsNummer letzteVollzogeneMutation) {
        this.letzteVollzogeneMutation = letzteVollzogeneMutation;
    }
    public List<AVBemerkung> getAvBemerkungen() {
        return avBemerkungen;
    }
    public void setAvBemerkungen(List<AVBemerkung> avBemerkungen) {
        this.avBemerkungen = avBemerkungen;
    }

}
