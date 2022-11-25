package org.optaplanner.examples.meetingscheduling.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

public class Room extends AbstractPersistable implements Labeled {

    private String name;
    private int capacity;

    public Room() {
    }

    public Room(long id, String name) {
        super(id);
        this.name = name;
    }

    public Room(long id, String name, int capacity) {
        this(id, name);
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
