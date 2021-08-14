package ch.so.agi.cadastralinfo.models.oereb;

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
}
