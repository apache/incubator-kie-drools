package org.drools.compiler.xpath;

public class Man extends Adult {

    private Woman wife;

    public Man(String name, int age) {
        super(name, age);
    }

    public Woman getWife() {
        return wife;
    }

    public void setWife(Woman wife) {
        this.wife = wife;
    }
}
