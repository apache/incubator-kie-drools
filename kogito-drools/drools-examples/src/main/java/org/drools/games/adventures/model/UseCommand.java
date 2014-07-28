package org.drools.games.adventures.model;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class UseCommand extends Command {
    private Character character;
    private Thing thing;
    private Thing target;

    public UseCommand(Character character, Thing thing, Thing target) {
        this.character = character;
        this.thing = thing;
        this.target = target;
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

    public Thing getTarget() {
        return target;
    }

    public void setTarget(Thing target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UseCommand that = (UseCommand) o;

        if (!character.equals(that.character)) { return false; }
        if (!target.equals(that.target)) { return false; }
        if (!thing.equals(that.thing)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = character.hashCode();
        result = 31 * result + thing.hashCode();
        result = 31 * result + target.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UseCommand{" +
               "character=" + character +
               ", thing=" + thing +
               ", target=" + target +
               '}';
    }
}
