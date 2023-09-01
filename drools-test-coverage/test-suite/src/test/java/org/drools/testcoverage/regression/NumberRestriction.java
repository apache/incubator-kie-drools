package org.drools.testcoverage.regression;

public class NumberRestriction {

    private Number value;

    public void setValue(Number number) {
        this.value = number;
    }

    public boolean isInt() {
        return value instanceof Integer;
    }

    public Number getValue() {
        return value;
    }

    public String getValueType() {
        return value.getClass().getName();
    }
}
