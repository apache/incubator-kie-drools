package org.drools.examples.fire;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Room {
    private String name;

    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Room{" +
               "name='" + name + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Room room = (Room) o;

        if (!name.equals(room.name)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
