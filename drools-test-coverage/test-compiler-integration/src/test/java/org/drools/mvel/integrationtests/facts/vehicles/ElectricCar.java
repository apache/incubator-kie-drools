package org.drools.mvel.integrationtests.facts.vehicles;

public class ElectricCar extends Vehicle<ElectricEngine> {

    private final ElectricEngine electricEngine;

    public ElectricCar(String maker, String model, int kw, int batterySize) {
        super(maker, model);
        this.electricEngine = new ElectricEngine(kw, batterySize);
    }

    @Override
    public ElectricEngine getEngine() {
        return electricEngine;
    }

}
