package org.optaplanner.examples.tennis.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Team.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Team extends AbstractPersistable implements Labeled {

    private String name;

    public Team() {
    }

    public Team(long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String toString() {
        return name == null ? super.toString() : name;
    }

}
