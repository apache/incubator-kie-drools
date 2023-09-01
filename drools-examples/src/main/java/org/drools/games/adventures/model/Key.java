package org.drools.games.adventures.model;

public class Key extends Thing {

    public Key(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "Key{id=" + getId() +", name=" + getName() + "} ";
    }
}
