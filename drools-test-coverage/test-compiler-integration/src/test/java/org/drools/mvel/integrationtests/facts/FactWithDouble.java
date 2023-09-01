package org.drools.mvel.integrationtests.facts;

public class FactWithDouble {

    private final double doubleValue;
    private final Double doubleObjectValue;

    public FactWithDouble(final double doubleValue) {
        this.doubleValue = doubleValue;
        this.doubleObjectValue = doubleValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public Double getDoubleObjectValue() {
        return doubleObjectValue;
    }
}