package org.optaplanner.examples.pas.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = RoomEquipment.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RoomEquipment extends AbstractPersistable {

    private Room room;
    private Equipment equipment;

    public RoomEquipment() {
    }

    public RoomEquipment(long id, Room room, Equipment equipment) {
        super(id);
        this.room = room;
        this.equipment = equipment;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return room + "-" + equipment;
    }

}
