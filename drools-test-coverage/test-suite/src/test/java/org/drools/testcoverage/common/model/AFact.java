package org.drools.testcoverage.common.model;

public class AFact {

    private String field1;
    private String field2;

    public AFact(final String field1, final String field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(final String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(final String field2) {
        this.field2 = field2;
    }

    public String toString() {
        return "A) " + field1 + ":" + field2;
    }
}
