package org.drools.scorecards.example;

import java.util.List;


public class Applicant {
    double age;
    String occupation;
    String  residenceState;
    double totalScore;
    boolean validLicense;

    public String getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes( String reasonCodes ) {
        this.reasonCodes = reasonCodes;
    }

    String reasonCodes;

    public boolean isValidLicense() {
        return validLicense;
    }

    public void setValidLicense(boolean validLicense) {
        this.validLicense = validLicense;
    }

    public Applicant() {
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getResidenceState() {
        return residenceState;
    }

    public void setResidenceState(String residenceState) {
        this.residenceState = residenceState;
    }
}
