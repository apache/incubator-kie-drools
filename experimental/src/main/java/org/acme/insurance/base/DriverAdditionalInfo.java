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
    private Integer         dayVehiclePlace;
    private Integer         nightVehiclePlace;
    private Integer         jobStatus;
    private Integer         residenceStatus;

    public Integer getDayVehiclePlace() {
        return dayVehiclePlace;
    }

    public void setDayVehiclePlace(Integer dayVehiclePlace) {
        this.dayVehiclePlace = dayVehiclePlace;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Integer getNightVehiclePlace() {
        return nightVehiclePlace;
    }

    public void setNightVehiclePlace(Integer nightVehiclePlace) {
        this.nightVehiclePlace = nightVehiclePlace;
    }

    public Integer getResidenceStatus() {
        return residenceStatus;
    }

    public void setResidenceStatus(Integer residenceStatus) {
        this.residenceStatus = residenceStatus;
    }

}
