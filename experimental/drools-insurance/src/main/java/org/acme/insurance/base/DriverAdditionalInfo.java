package org.acme.insurance.base;

public class DriverAdditionalInfo {

    public static final int STREET             = 0;
    public static final int GARAGE             = 1;
    public static final int PUBLICPARKING      = 2;

    public static final int RENTEDHOME         = 0;
    public static final int OWNHOME            = 1;

    public static final int UNEMPLOYED         = 0;
    public static final int STUDENT            = 1;
    public static final int GOVERNMENTEMPLOYEE = 2;
    public static final int PRIVATEEMPLOYEE    = 3;
    public static final int BUSINESSOWNER      = 4;

    private int             driverId;
    private int         dayVehiclePlace;
    private int         nightVehiclePlace;
    private int         jobStatus;
    private int         residenceStatus;

    public int getDayVehiclePlace() {
        return dayVehiclePlace;
    }

    public void setDayVehiclePlace(int dayVehiclePlace) {
        this.dayVehiclePlace = dayVehiclePlace;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(int jobStatus) {
        this.jobStatus = jobStatus;
    }

    public int getNightVehiclePlace() {
        return nightVehiclePlace;
    }

    public void setNightVehiclePlace(int nightVehiclePlace) {
        this.nightVehiclePlace = nightVehiclePlace;
    }

    public int getResidenceStatus() {
        return residenceStatus;
    }

    public void setResidenceStatus(int residenceStatus) {
        this.residenceStatus = residenceStatus;
    }

}
