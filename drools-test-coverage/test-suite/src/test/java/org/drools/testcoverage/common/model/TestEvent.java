package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class TestEvent implements Serializable {

    private static final long serialVersionUID = -6985691286327371275L;

    private final Integer id;
    private final String name;
    private Serializable value;

    public TestEvent(final Integer id, final String name, final Serializable value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Serializable getValue() {
        return value;
    }

    public void setValue(final Serializable value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("TestEvent[id=%s, name=%s, value=%s]", id, name, value);
    }
}
