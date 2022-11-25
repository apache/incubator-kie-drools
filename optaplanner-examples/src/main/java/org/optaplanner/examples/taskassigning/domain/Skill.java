package org.optaplanner.examples.taskassigning.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Skill extends AbstractPersistable implements Labeled {

    private String name;

    public Skill() {
    }

    public Skill(long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
