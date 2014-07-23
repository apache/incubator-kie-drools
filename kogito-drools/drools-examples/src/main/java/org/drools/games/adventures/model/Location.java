package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Location {
    @Position(0)
    private Thing thing;

    @Position(1)
    private Thing target;

    public Location(Thing thing, Thing target) {
        this.thing = thing;
        this.target = target;
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

        Location location = (Location) o;

        if (!target.equals(location.target)) { return false; }
        if (!thing.equals(location.thing)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = thing.hashCode();
        result = 31 * result + target.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Location{" +
               "target=" + target +
               ", thing=" + thing +
               '}';
    }
}
