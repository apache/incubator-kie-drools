package org.drools.mvel.integrationtests.facts;

public class FactWithFloat {

    private final float floatValue;
    private final Float floatObjectValue;

    public FactWithFloat(final float floatValue) {
        this.floatValue = floatValue;
        this.floatObjectValue = floatValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public Float getFloatObjectValue() {
        return floatObjectValue;
    }
}