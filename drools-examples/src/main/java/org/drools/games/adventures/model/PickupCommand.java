package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;
import org.drools.games.adventures.model.Character;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class PickupCommand extends Command {
    @Position(1)
    private Character character;

    @Position(2)
    private Thing     thing;

    public PickupCommand(Character character, Thing thing) {
        this.character = character;
        this.thing = thing;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }

        PickupCommand that = (PickupCommand) o;

        if (!character.equals(that.character)) { return false; }
        if (!thing.equals(that.thing)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + character.hashCode();
        result = 31 * result + thing.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PickupCommand{" +
               "character=" + character +
               ", thing=" + thing +
               '}';
    }
}
