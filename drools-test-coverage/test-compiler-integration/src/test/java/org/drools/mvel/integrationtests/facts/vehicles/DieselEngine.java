package org.drools.mvel.integrationtests.facts.vehicles;

public class DieselEngine extends Engine {

    private boolean dirty;

    private final boolean adBlueRequired;

    public DieselEngine(int kw, boolean adBlueRequired) {
        super(kw);
        this.adBlueRequired = adBlueRequired;
    }

    public boolean isAdBlueRequired() {
        return adBlueRequired;
    }

    @Override
    boolean isZeroEmissions() {
        return false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

}
