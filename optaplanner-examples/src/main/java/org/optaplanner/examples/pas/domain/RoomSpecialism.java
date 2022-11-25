package org.optaplanner.examples.pas.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = RoomSpecialism.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RoomSpecialism extends AbstractPersistable {

    private Room room;
    private Specialism specialism;
    private int priority; // AKA choice

    public RoomSpecialism() {
    }

    public RoomSpecialism(long id, Room room, Specialism specialism, int priority) {
        super(id);
        this.room = room;
        this.specialism = specialism;
        this.priority = priority;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Specialism getSpecialism() {
        return specialism;
    }

    public void setSpecialism(Specialism specialism) {
        this.specialism = specialism;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return room + "-" + specialism;
    }

}
