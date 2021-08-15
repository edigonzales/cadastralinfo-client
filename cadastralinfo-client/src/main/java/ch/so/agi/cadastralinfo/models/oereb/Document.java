package ch.so.agi.cadastralinfo.models.oereb;

import java.util.Objects;

import static elemental2.dom.DomGlobal.console;

public class Document {
    private String title;
    
    private String officialTitle;
    
    private String officialNumber;
    
    private String abbreviation;
    
    private String textAtWeb;
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getTextAtWeb() {
        return textAtWeb;
    }

    public void setTextAtWeb(String textAtWeb) {
        this.textAtWeb = textAtWeb;
    }

    public String getOfficialTitle() {
        return officialTitle;
    }

    public void setOfficialTitle(String officialTitle) {
        this.officialTitle = officialTitle;
    }

    public String getOfficialNumber() {
        return officialNumber;
    }

    public void setOfficialNumber(String officialNumber) {
        this.officialNumber = officialNumber;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        Document document = (Document) o;
        return Objects.equals(textAtWeb, document.textAtWeb);
    }
    
    @Override
    public int hashCode() {
        return textAtWeb.hashCode();
    }
}
