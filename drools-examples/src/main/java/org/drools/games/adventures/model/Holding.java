package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;

public class Holding {
    @Position(0)
    private Character character;

    @Position(1)
    private Thing     thing;

    public Holding(Character character, Thing thing) {
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

        Holding holding = (Holding) o;

        if (!character.equals(holding.character)) { return false; }
        if (!thing.equals(holding.thing)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = character.hashCode();
        result = 31 * result + thing.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Holding{" +
               "character=" + character +
               ", thing=" + thing +
               '}';
    }
}
