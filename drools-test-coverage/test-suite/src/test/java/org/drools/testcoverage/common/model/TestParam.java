package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class TestParam implements Serializable {

    private String value1;
    private String value2;
    private Object[] elements;

    public String getValue1() {
        return this.value1;
    }

    public void setValue1(final String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return this.value2;
    }

    public void setValue2(final String value2) {
        this.value2 = value2;
    }

    public Object[] getElements() {
        return elements;
    }

    public void setElements(final Object[] elements) {
        this.elements = elements;
    }
}
