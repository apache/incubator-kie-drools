package org.drools.examples.fire;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Alarm {
    private String name;

    public Alarm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Alarm{" +
               "name='" + name + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Alarm alarm = (Alarm) o;

        if (!name.equals(alarm.name)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
