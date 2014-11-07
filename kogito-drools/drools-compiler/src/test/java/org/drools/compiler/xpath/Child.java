package org.drools.compiler.xpath;

import java.util.ArrayList;
import java.util.List;

public class Child extends Person {

    private final List<Toy> toys = new ArrayList<Toy>();

    public Child(String name, int age) {
        super(name, age);
    }

    public List<Toy> getToys() {
        return toys;
    }

    public void addToy(Toy toy) {
        toys.add(toy);
    }
}
