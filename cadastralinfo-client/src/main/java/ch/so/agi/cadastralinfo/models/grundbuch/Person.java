package ch.so.agi.cadastralinfo.models.grundbuch;

import java.util.List;

public abstract class Person {
    private String nummer;
    private List<Adresse> adressen;

    public String getNummer() {
        return nummer;
    }

    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    public List<Adresse> getAdressen() {
        return adressen;
    }

    public void setAdressen(List<Adresse> adressen) {
        this.adressen = adressen;
    }
}
