package org.drools.mvel.compiler;

public class Cat extends Pet {

    public Cat(String ownerName) {
        super(ownerName);
    }

    public String getBreed() {
        return "Siamise";
    }
}
