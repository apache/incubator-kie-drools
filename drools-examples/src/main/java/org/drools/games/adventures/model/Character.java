package org.drools.games.adventures.model;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Character extends Thing {


    public Character(String name) {
        super(name);
    }

    public Character(long id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return "Character{id=" + getId() +", name=" + getName() + "} ";
    }
}
