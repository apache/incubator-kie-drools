package org.drools.model.codegen.execmodel.fireandalarm.model;

public class Fire {
    private Room room;

    public Fire() { }

    public Fire(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
