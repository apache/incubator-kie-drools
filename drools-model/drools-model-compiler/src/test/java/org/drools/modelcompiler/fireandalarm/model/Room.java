package org.drools.modelcompiler.fireandalarm.model;

public class Room {
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
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Room)) return false;
        return name.equals(((Room)obj).getName());
    }

    @Override
    public String toString() {
        return name;
    }
}
