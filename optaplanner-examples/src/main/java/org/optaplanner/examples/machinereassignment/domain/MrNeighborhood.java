package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class MrNeighborhood extends AbstractPersistable {

    @SuppressWarnings("unused")
    MrNeighborhood() {
    }

    public MrNeighborhood(long id) {
        super(id);
    }
}
