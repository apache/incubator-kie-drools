package org.drools.compiler.xpath;

public class Woman extends Adult {

    private String husband;

    public Woman(String name, int age) {
        super(name, age);
    }

    public String getHusband() {
        return husband;
    }

    public void setHusband(String husband) {
        this.husband = husband;
    }
}
