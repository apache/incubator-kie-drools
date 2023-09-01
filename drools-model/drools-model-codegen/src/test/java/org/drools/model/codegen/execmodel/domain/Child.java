package org.drools.model.codegen.execmodel.domain;

import java.util.List;

import org.drools.core.phreak.ReactiveList;

public class Child extends Person {

    private final String parent;

    private final List<Toy> toys = new ReactiveList<Toy>();

    public Child(String name, int age) {
        this(name, age, null);
    }

    public Child(String name, int age, String parent) {
        super(name, age);
        this.parent = parent;
    }

    public List<Toy> getToys() {
        return toys;
    }

    public void addToy(Toy toy) {
        toys.add(toy);
    }

    public String getParent() {
        return parent;
    }
}