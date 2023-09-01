package org.drools.ruleunits.impl.domain;

import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;

import java.util.Objects;

@PropertyReactive
public class Location {
    @Position(0)
    public String thing;

    @Position(1)
    public String location;

    public boolean inferred;

    public Location(String thing, String location) {
        this(thing, location, false);
        this.thing = thing;
        this.location = location;
    }

    public Location(String thing, String location, boolean inferred) {
        this.thing = thing;
        this.location = location;
        this.inferred = inferred;
    }

    public String getThing() {
        return thing;
    }

    public void setThing(String thing) {
        this.thing = thing;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isInferred() { return inferred; }

    public void setInferred(boolean inferred) { this.inferred = inferred; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location1 = (Location) o;
        return inferred == location1.inferred && Objects.equals(thing, location1.thing) && Objects.equals(location, location1.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thing, location, inferred);
    }

    @Override
    public String toString() {
        return thing + " in " + location;
    }
}
