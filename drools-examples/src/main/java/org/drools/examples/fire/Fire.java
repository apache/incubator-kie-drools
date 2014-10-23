package org.drools.examples.fire;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Fire {
    private Room room;

    public Fire(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "Fire{" +
               "room=" + room +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Fire fire = (Fire) o;

        if (!room.equals(fire.room)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return room.hashCode();
    }
}
