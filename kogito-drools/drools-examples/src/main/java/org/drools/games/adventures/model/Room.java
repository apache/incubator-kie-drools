package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Room extends Thing {

    public Room(String name) {
        super(name);
    }

    public Room(long id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return "Room{id=" + getId() +", name=" + getName() + "} ";
    }
}
