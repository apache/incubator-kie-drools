package org.drools.mvel.integrationtests.facts;

public class FactWithShort {

    private final short shortValue;
    private final Short shortObjectValue;

    public FactWithShort(final short shortValue) {
        this.shortValue = shortValue;
        this.shortObjectValue = shortValue;
    }

    public short getShortValue() {
        return shortValue;
    }

    public Short getShortObjectValue() {
        return shortObjectValue;
    }
}
