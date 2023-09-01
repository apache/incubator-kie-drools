package org.drools.games.adventures.model;

public class Room extends Thing {

    public Room(String name) {
        super(name, false);
    }

    public Room(long id, String name) {
        super(id, name, false);
    }

    @Override
    public String toString() {
        return "Room{id=" + getId() +", name=" + getName() + "} ";
    }
}
