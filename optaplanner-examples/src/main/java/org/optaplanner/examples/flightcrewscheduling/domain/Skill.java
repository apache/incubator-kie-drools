package org.optaplanner.examples.flightcrewscheduling.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class Skill extends AbstractPersistable {

    private String name;

    public Skill() {
    }

    @Override
    public String toString() {
        return name;
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
