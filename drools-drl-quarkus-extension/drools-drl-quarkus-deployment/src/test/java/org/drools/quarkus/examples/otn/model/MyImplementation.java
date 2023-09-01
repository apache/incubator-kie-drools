package org.drools.quarkus.examples.otn.model;

public class MyImplementation implements MyInterface {

    private final String val;

    public MyImplementation(String val) {
        this.val = val;
    }

    @Override
    public String getVal() {
        return val;
    }
    
}
