package org.drools.mvel.integrationtests.facts;

public class ClassA implements InterfaceA {

    private ClassB b = null;

    public ClassB getB() {
        return b;
    }

    public void setB(final InterfaceB b) {
        this.b = (ClassB) b;
    }

}
