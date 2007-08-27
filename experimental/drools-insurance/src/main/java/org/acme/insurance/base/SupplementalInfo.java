package org.acme.insurance.base;

public class SupplementalInfo {

    private int     driverId;

    private boolean extraCar;
    private boolean extraAssistence;
    private boolean glassCoverage;
    private boolean nonRelatedExpenses;


    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public boolean isExtraAssistence() {
        return extraAssistence;
    }

    public void setExtraAssistence(boolean extraAssistence) {
        this.extraAssistence = extraAssistence;
    }

    public boolean isExtraCar() {
        return extraCar;
    }

    public void setExtraCar(boolean extraCar) {
        this.extraCar = extraCar;
    }

    public boolean isGlassCoverage() {
        return glassCoverage;
    }

    public void setGlassCoverage(boolean glassCoverage) {
        this.glassCoverage = glassCoverage;
    }

    public boolean isNonRelatedExpenses() {
        return nonRelatedExpenses;
    }

    public void setNonRelatedExpenses(boolean nonRelatedExpenses) {
        this.nonRelatedExpenses = nonRelatedExpenses;
    }
}
