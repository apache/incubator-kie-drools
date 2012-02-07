package org.jboss.qa.brms.persistence;

import java.io.Serializable;

/**
 * Simple event to store some values for complex event processing tests.
 * Feel free to add more fields if neccessary.
 *
 * @author tschloss
 */
public class TestEvent implements Serializable {
    private static final long serialVersionUID = -6985691286327371275L;

    private final Integer id;
    private final String name;
    private Object value;

    public TestEvent(Integer id, String name, Object value) {
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("TestEvent[id=%s, name=%s, value=%s]", id, name, value);
    }
}
