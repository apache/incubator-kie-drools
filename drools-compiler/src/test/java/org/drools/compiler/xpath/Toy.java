package org.drools.compiler.xpath;

import org.drools.core.phreak.ReactiveObject;

public class Toy extends ReactiveObject {

    private final String name;

    private String owner;

    public Toy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
