package org.test.domain.fireandalarm;

import java.io.Serializable;

public class Room implements Serializable {

    private String name;

    public Room() { }

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
        return name;
    }
}
