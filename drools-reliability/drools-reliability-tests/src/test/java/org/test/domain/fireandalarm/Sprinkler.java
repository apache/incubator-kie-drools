package org.test.domain.fireandalarm;

import java.io.Serializable;

public class Sprinkler implements Serializable {
    private Room room;
    private boolean on = false;

    public Sprinkler() { }

    public Sprinkler(Room room) {
        this.room = room;
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
        return "Sprinkler for " + room;
    }
}
