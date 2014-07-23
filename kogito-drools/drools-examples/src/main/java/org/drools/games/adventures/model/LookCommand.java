package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;
import org.drools.games.adventures.model.Character;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class LookCommand extends Command {

    @Position(1)
    private Character character;

    public LookCommand(Character character) {
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }

        LookCommand that = (LookCommand) o;

        if (!character.equals(that.character)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + character.hashCode();
        return result;
    }


}
