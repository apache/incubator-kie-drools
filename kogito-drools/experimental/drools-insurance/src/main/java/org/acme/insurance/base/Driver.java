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

    private int id;

    private String name;
    private Date birhDate;
    private int licenceYears;

    private int priorClaims;
    private int maritalState;
    private boolean hasChildren;
    private int genre;

    private double insuranceFactor = 1.0;

    public double getInsuranceFactor() {
        return insuranceFactor;
    }

    public void setInsuranceFactor(double factor) {
        updateInsuranceFactor(factor);
    }

    public void updateInsuranceFactor(double factor) {
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

    public int getLicenceYears() {
        return licenceYears;
    }

    public void setLicenceYears(int licenceYears) {
        this.licenceYears = licenceYears;
    }

    public int getMaritalState() {
        return maritalState;
    }

    public void setMaritalState(int maritalState) {
        this.maritalState = maritalState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriorClaims() {
        return priorClaims;
    }

    public void setPriorClaims(int priorClaims) {
        this.priorClaims = priorClaims;
    }
}
