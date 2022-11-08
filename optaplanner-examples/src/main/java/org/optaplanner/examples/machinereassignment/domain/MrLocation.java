package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = MrLocation.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MrLocation extends AbstractPersistableJackson {

    @SuppressWarnings("unused")
    MrLocation() { // For Jackson.
    }

    public MrLocation(long id) {
        super(id);
    }
}
