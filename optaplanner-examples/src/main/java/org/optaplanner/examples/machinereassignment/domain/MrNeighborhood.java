package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = MrNeighborhood.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MrNeighborhood extends AbstractPersistableJackson {

    @SuppressWarnings("unused")
    MrNeighborhood() { // For Jackson.
    }

    public MrNeighborhood(long id) {
        super(id);
    }
}
