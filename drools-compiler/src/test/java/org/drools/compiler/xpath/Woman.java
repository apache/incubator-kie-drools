package org.drools.compiler.xpath;

public class Woman extends Adult {

    private Man husband;

    public Woman(String name, int age) {
        super(name, age);
    }

    public Man getHusband() {
        return husband;
    }

    public void setHusband(Man husband) {
        this.husband = husband;
    }
}
