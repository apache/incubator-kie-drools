package org.drools.mvel.integrationtests.facts;

public class FactWithInteger {

    private final int intValue;
    private final Integer integerValue;

    public FactWithInteger(final int intValue) {
        this.intValue = intValue;
        this.integerValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }
}