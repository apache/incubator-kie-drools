package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;
import org.drools.games.adventures.model.Character;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class MoveCommand extends Command {

    @Position(1)
    private Character character;

    @Position(2)
    private Room      room;



    public MoveCommand(Character character, Room room) {
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

        MoveCommand that = (MoveCommand) o;

        if (!character.equals(that.character)) { return false; }
        if (!room.equals(that.room)) { return false; }

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
        return "MoveCommand{" +
               "character=" + character +
               ", room=" + room +
               '}';
    }
}
