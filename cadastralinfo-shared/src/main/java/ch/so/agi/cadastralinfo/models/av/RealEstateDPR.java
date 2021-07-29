package ch.so.agi.cadastralinfo.models.av;

import java.util.ArrayList;

// TODO: rename to GetParcelByIdResponse?
public class RealEstateDPR {
    private String realEstateType;
    
    private String number;
    
    private String identND;
    
    private String egrid;
    
    private String canton;
    
    private String municipality;
    
    private String subunitOfLandRegister;
    
    private int fosnNr;
    
    private Integer landRegistryArea;
    
    private String limit;
                
    private String cadastrePdfExtractUrl;
    
//    private Office cadastreCadastreAuthority;
//
//    private Office cadastreLandRegisterOffice;
//
//    private Office cadastreSurveyorOffice;
//    
//    private ArrayList<String> localNames;
//    
//    private ArrayList<LandCoverShare> landCoverShares;
//    
//    private ArrayList<Building> buildings;
    
    public String getRealEstateType() {
        return realEstateType;
    }

    public void setRealEstateType(String realEstateType) {
        this.realEstateType = realEstateType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIdentND() {
        return identND;
    }

    public void setIdentND(String identND) {
        this.identND = identND;
    }

    public String getEgrid() {
        return egrid;
    }

    public void setEgrid(String egrid) {
        this.egrid = egrid;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getSubunitOfLandRegister() {
        return subunitOfLandRegister;
    }

    public void setSubunitOfLandRegister(String subunitOfLandRegister) {
        this.subunitOfLandRegister = subunitOfLandRegister;
    }

    public int getFosnNr() {
        return fosnNr;
    }

    public void setFosnNr(int fosnNr) {
        this.fosnNr = fosnNr;
    }

    public Integer getLandRegistryArea() {
        return landRegistryArea;
    }

    public void setLandRegistryArea(Integer landRegistryArea) {
        this.landRegistryArea = landRegistryArea;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }


    public String getCadastrePdfExtractUrl() {
        return cadastrePdfExtractUrl;
    }

    public void setCadastrePdfExtractUrl(String cadastrePdfExtractUrl) {
        this.cadastrePdfExtractUrl = cadastrePdfExtractUrl;
    }

//    public Office getCadastreCadastreAuthority() {
//        return cadastreCadastreAuthority;
//    }
//
//    public void setCadastreCadastreAuthority(Office cadastreCadastreAuthority) {
//        this.cadastreCadastreAuthority = cadastreCadastreAuthority;
//    }
//
//    public Office getCadastreLandRegisterOffice() {
//        return cadastreLandRegisterOffice;
//    }
//
//    public void setCadastreLandRegisterOffice(Office cadastreLandRegisterOffice) {
//        this.cadastreLandRegisterOffice = cadastreLandRegisterOffice;
//    }
//
//    public Office getCadastreSurveyorOffice() {
//        return cadastreSurveyorOffice;
//    }
//
//    public void setCadastreSurveyorOffice(Office cadastreSurveyorOffice) {
//        this.cadastreSurveyorOffice = cadastreSurveyorOffice;
//    }
//
//    public ArrayList<String> getLocalNames() {
//        return localNames;
//    }
//
//    public void setLocalNames(ArrayList<String> localNames) {
//        this.localNames = localNames;
//    }
//
//    public ArrayList<LandCoverShare> getLandCoverShares() {
//        return landCoverShares;
//    }
//
//    public void setLandCoverShares(ArrayList<LandCoverShare> landCoverShares) {
//        this.landCoverShares = landCoverShares;
//    }
//
//    public ArrayList<Building> getBuildings() {
//        return buildings;
//    }
//
//    public void setBuildings(ArrayList<Building> buildings) {
//        this.buildings = buildings;
//    }
}
