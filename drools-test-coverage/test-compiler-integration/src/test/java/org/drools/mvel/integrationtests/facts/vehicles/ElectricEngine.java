package org.drools.mvel.integrationtests.facts.vehicles;

public class ElectricEngine extends Engine {

    private final int batterySize;

    public ElectricEngine(int kw, int batterySize) {
        super(kw);
        this.batterySize = batterySize;
    }

    @Override
    boolean isZeroEmissions() {
        return true;
    }

    public int getBatterySize() {
        return batterySize;
    }

}
