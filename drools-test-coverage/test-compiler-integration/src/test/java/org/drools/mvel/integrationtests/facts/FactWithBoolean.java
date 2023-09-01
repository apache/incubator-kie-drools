package org.drools.mvel.integrationtests.facts;

public class FactWithBoolean {

    private final boolean booleanValue;
    private final Boolean booleanObjectValue;

    public FactWithBoolean(final boolean booleanValue) {
        this.booleanValue = booleanValue;
        this.booleanObjectValue = booleanValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public Boolean getBooleanObjectValue() {
        return booleanObjectValue;
    }
}
