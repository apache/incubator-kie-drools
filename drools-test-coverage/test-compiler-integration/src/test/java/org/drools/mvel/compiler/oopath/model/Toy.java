package org.drools.mvel.compiler.oopath.model;

import org.drools.core.phreak.AbstractReactiveObject;

public class Toy extends AbstractReactiveObject {

    private String name;

    private String owner;

    public Toy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
        notifyModification();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Toy: " + name;
    }
}
