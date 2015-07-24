package org.drools.compiler.integrationtests;


public class NonStringConstructorClass {
    private String something;
    
    public String getSomething() {
        return something;
    }
    
    public void setSomething(String something) {
        this.something = something;
    }

    @Override
    public String toString() {
        return "NonStringConstructorClass [something=" + something + "]";
    }
}
