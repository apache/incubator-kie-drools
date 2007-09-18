package org.acme.insurance.base;

public class AccessoriesCoverage {

    private int driverId;

    private double soundSystemValue;
    private double armorValue;
    private double alarmSystemValue;

    public double getArmorValue() {
        return armorValue;
    }

    public void setArmorValue(double armorValue) {
        this.armorValue = armorValue;
    }

    public double getSoundSystemValue() {
        return soundSystemValue;
    }

    public void setSoundSystemValue(double soundSystemValue) {
        this.soundSystemValue = soundSystemValue;
    }

    public double getAlarmSystemValue() {
        return alarmSystemValue;
    }

    public void setAlarmSystemValue(double alarmSystemValue) {
        this.alarmSystemValue = alarmSystemValue;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
}
