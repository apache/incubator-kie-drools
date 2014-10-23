package org.drools.examples.fire;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Sprinkler {
    private Room room;
    private boolean on;

    public Sprinkler(Room room) {
        this.room = room;
    }

    public Sprinkler(Room room, boolean on) {
        this.room = room;
        this.on = on;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public String toString() {
        return "Sprinkler{" +
               "room=" + room +
               ", on=" + on +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Sprinkler sprinkler = (Sprinkler) o;

        if (on != sprinkler.on) { return false; }
        if (!room.equals(sprinkler.room)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = room.hashCode();
        result = 31 * result + (on ? 1 : 0);
        return result;
    }
}
