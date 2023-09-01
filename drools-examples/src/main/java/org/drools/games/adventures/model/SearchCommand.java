package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;

public class SearchCommand extends Command {
    @Position(1)
    private Thing     thing;

    public SearchCommand() {

    }

    public SearchCommand(Thing thing) {
        this.thing = thing;
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

        SearchCommand that = (SearchCommand) o;

        return thing != null ? thing.equals(that.thing) : that.thing == null;

    }

    @Override
    public int hashCode() {
        return thing != null ? thing.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SearchCommand{" +
               ", thing=" + thing +
               '}';
    }
}
