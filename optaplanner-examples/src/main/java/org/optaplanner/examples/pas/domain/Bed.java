package org.optaplanner.examples.pas.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Bed.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Bed extends AbstractPersistable implements Labeled {

    private Room room;
    private int indexInRoom;

    public Bed() {
    }

    public Bed(long id, Room room, int indexInRoom) {
        super(id);
        this.room = room;
        this.indexInRoom = indexInRoom;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getIndexInRoom() {
        return indexInRoom;
    }

    public void setIndexInRoom(int indexInRoom) {
        this.indexInRoom = indexInRoom;
    }

    @JsonIgnore
    public String getLabelInRoom() {
        if (indexInRoom > 'Z') {
            return Integer.toString(indexInRoom);
        }
        return Character.toString((char) ('A' + indexInRoom));
    }

    @Override
    public String getLabel() {
        return room.getDepartment().getName() + " " + room.getName() + " " + getLabelInRoom();
    }

    @Override
    public String toString() {
        return room + "(" + indexInRoom + ")";
    }

}
