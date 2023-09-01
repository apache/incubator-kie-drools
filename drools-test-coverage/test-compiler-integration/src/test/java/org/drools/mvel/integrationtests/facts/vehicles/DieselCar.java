package org.drools.mvel.integrationtests.facts.vehicles;

public class DieselCar extends Vehicle<DieselEngine> {

    private final DieselEngine engine;

    public DieselCar(String maker, String model, int kw, boolean adBlueRequired) {
        super(maker, model);
        this.engine = new DieselEngine(kw, adBlueRequired);
    }

    @Override
    public DieselEngine getEngine() {
        return engine;
    }

}
