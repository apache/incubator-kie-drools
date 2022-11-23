package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class MrNeighborhood extends AbstractPersistableJackson {

    @SuppressWarnings("unused")
    MrNeighborhood() { // For Jackson.
    }

    public MrNeighborhood(long id) {
        super(id);
    }
}
