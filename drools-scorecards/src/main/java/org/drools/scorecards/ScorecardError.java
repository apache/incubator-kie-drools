package org.drools.scorecards;

import java.io.Serializable;


public class ScorecardError implements Serializable {
    private String errorLocation;
    private String errorMessage;

    public ScorecardError(String errorLocation, String errorMessage) {
        this.errorLocation = errorLocation;
        this.errorMessage = errorMessage;
    }

    public ScorecardError() {
    }

    public String getErrorLocation() {
        return errorLocation;
    }

    public void setErrorLocation(String errorLocation) {
        this.errorLocation = errorLocation;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
