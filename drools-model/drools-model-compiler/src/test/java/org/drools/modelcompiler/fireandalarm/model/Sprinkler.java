package org.drools.modelcompiler.fireandalarm.model;

public class Sprinkler {
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
    public int hashCode() {
        return room.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sprinkler)) return false;
        return room.equals(((Sprinkler)obj).getRoom());
    }

    @Override
    public String toString() {
        return "Sprinkler for " + room;
    }
}
