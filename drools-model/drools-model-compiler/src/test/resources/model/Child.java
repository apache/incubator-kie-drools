package org.model;

public class Child extends Person {
    private final int toysNr;

    public Child(String name, int age, int toysNr) {
        super( name, age );
        this.toysNr = toysNr;
    }

    public int getToysNr() {
        return toysNr;
    }
}