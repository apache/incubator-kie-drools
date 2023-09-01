package org.drools.testcoverage.common.model;

import java.util.Date;
import java.util.Objects;

public class ChildEventA extends ParentEvent {

    private String name;

    public ChildEventA(Date eventTimestamp, String name) {
        super(eventTimestamp);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChildEventA aEvent = (ChildEventA) o;
        return Objects.equals(name, aEvent.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ChildEventA{" +
               "name='" + name + '\'' +
               '}';
    }
}
