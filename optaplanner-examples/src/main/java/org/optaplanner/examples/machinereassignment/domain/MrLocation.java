package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class MrLocation extends AbstractPersistableJackson {

    @SuppressWarnings("unused")
    MrLocation() { // For Jackson.
    }

    public MrLocation(long id) {
        super(id);
    }
}
