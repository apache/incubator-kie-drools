package org.acme.insurance.base;

public class AccessoriesCoverage {

    private int driverId;
    
    private Double soundSystemValue;
    private Double armorValue;
    private Double alarmSystemValue;
    
    public Double getArmorValue() {
        return armorValue;
    }
    public void setArmorValue(Double armorValue) {
        this.armorValue = armorValue;
    }
    
    public Double getSoundSystemValue() {
        return soundSystemValue;
    }
    public void setSoundSystemValue(Double soundSystemValue) {
        this.soundSystemValue = soundSystemValue;
    }
    public Double getAlarmSystemValue() {
        return alarmSystemValue;
    }
    public void setAlarmSystemValue(Double alarmSystemValue) {
        this.alarmSystemValue = alarmSystemValue;
    }
    public int getDriverId() {
        return driverId;
    }
    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
    
    
    
}
