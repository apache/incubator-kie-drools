package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;
import org.drools.games.adventures.model.Character;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class EnterEvent extends GameEvent  {

    @Position(0)
    private Character character;

    @Position(1)
    private Room      room;

    public EnterEvent(Character character, Room room) {
        this.character = character;
        this.room = room;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        EnterEvent enterEvent = (EnterEvent) o;

        if (!character.equals(enterEvent.character)) { return false; }
        if (!room.equals(enterEvent.room)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = character.hashCode();
        result = 31 * result + room.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EnterEvent{" +
               "character=" + character +
               ", room=" + room +
               '}';
    }
}
