package org.test.domain.fireandalarm;

import java.io.Serializable;

public class Fire implements Serializable {
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
