package org.drools.mvel.integrationtests.facts;

public class FactWithLong {

    private final long longValue;
    private final Long longObjectValue;

    public FactWithLong(final long longValue) {
        this.longValue = longValue;
        this.longObjectValue = longValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public Long getLongObjectValue() {
        return longObjectValue;
    }
}
