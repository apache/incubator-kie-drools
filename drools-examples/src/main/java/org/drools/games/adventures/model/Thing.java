package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Thing {
    @Position(0)
    private long id;

    @Position(1)
    private String name;

    public Thing(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Thing(String name) {
        this(-1, name);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Thing thing = (Thing) o;

        if (id != thing.id) { return false; }
        if (!name.equals(thing.name)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Thing{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}
