package org.acme.insurance.base;

import java.text.ParseException;
import java.util.Date;

/**
 * This represents obviously a driver who is applying for an insurance Policy.
 */
public class Driver {
    
    public static final int MALE = 0;
    public static final int FEMALE = 1;
    
    public static final int SINGLE = 0;
    public static final int MARRIED = 1;
    
    private int     id;

    private String  name;
    private Date    birhDate;
    private Integer licenceYears;

    private Integer priorClaims;
    private Integer maritalState;
    private boolean hasChildren;
    private Integer genre;
    
    private Double  insuranceFactor = 1.0;
    
    public Double getInsuranceFactor(){
        return insuranceFactor;
    }
    
    public void updateInsuranceFactor(Double factor) {
        this.insuranceFactor *= factor;
    }
    
    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    public int getAge() throws ParseException {
        return (int) (((new Date()).getTime() - birhDate.getTime()) / 86400000L / 360);
    }

    public Date getBirhDate() {
        return birhDate;
    }

    public void setBirhDate(Date birhDate) {
        this.birhDate = birhDate;
    }

    public boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getLicenceYears() {
        return licenceYears;
    }

    public void setLicenceYears(Integer licenceYears) {
        this.licenceYears = licenceYears;
    }

    public Integer getMaritalState() {
        return maritalState;
    }

    public void setMaritalState(Integer maritalState) {
        this.maritalState = maritalState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriorClaims() {
        return priorClaims;
    }

    public void setPriorClaims(Integer priorClaims) {
        this.priorClaims = priorClaims;
    }
}
