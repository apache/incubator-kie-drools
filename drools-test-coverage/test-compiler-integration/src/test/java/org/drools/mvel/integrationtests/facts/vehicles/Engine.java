package org.drools.mvel.integrationtests.facts.vehicles;

public abstract class Engine {

    private final int kw;

    public Engine(int kw) {
        this.kw = kw;
    }

    public int getKw() {
        return kw;
    }

    abstract boolean isZeroEmissions();

}
