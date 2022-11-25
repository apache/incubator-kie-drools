package org.optaplanner.examples.curriculumcourse.domain;

import java.util.Objects;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Room.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Room extends AbstractPersistable implements Labeled {

    private String code;
    private int capacity;

    public Room() {
    }

    public Room(int id, String code, int capacity) {
        super(id);
        this.code = Objects.requireNonNull(code);
        this.capacity = capacity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String getLabel() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }

}
