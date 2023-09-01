package org.drools.mvel.integrationtests.facts;

import java.math.BigDecimal;

public class FactWithBigDecimal {

    private final BigDecimal bigDecimalValue;

    public FactWithBigDecimal(final BigDecimal bigDecimalValue) {
        this.bigDecimalValue = bigDecimalValue;
    }

    public BigDecimal getBigDecimalValue() {
        return bigDecimalValue;
    }
}