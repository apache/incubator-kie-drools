package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;

public class GiveEvent extends GameEvent {
    @Position(0)
    private Character giver;

    @Position(1)
    private Thing     thing;

    @Position(2)
    private Character receiver;

    public GiveEvent(Character giver, Thing thing, Character receiver) {
        this.giver = giver;
        this.thing = thing;
        this.receiver = receiver;
    }

    public Character getGiver() {
        return giver;
    }

    public void setGiver(Character giver) {
        this.giver = giver;
    }

    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }

    public Character getReceiver() {
        return receiver;
    }

    public void setReceiver(Character receiver) {
        this.receiver = receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        GiveEvent giveEvent = (GiveEvent) o;

        if (!giver.equals(giveEvent.giver)) { return false; }
        if (!receiver.equals(giveEvent.receiver)) { return false; }
        if (!thing.equals(giveEvent.thing)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = giver.hashCode();
        result = 31 * result + thing.hashCode();
        result = 31 * result + receiver.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GiveEvent{" +
               "receiver=" + receiver +
               ", thing=" + thing +
               ", giver=" + giver +
               '}';
    }
}
