package org.drools.games.adventures.model;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Key extends Thing {

    public Key(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "Key{id=" + getId() +", name=" + getName() + "} ";
    }
}
