package org.drools.compiler.xpath;

import org.drools.core.phreak.ReactiveList;

import java.util.List;

public class Child extends Person {

    private final List<Toy> toys = new ReactiveList<Toy>();

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
