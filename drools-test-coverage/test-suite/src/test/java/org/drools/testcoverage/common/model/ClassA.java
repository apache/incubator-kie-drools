package org.drools.testcoverage.common.model;

public class ClassA implements InterfaceA {

    private ClassB b = null;

    public ClassB getB() {
        return b;
    }

    public void setB(final InterfaceB b) {
        this.b = (ClassB) b;
    }

}
