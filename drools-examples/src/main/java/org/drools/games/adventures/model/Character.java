package org.drools.games.adventures.model;

public class Character extends Thing {


    public Character(String name) {
        super(name, false);
    }

    public Character(long id, String name) {
        super(id, name, false);
    }

    @Override
    public String toString() {
        return "Character{id=" + getId() +", name=" + getName() + "} ";
    }
}
