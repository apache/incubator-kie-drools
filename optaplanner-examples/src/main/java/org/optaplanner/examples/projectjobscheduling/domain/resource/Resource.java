package org.optaplanner.examples.projectjobscheduling.domain.resource;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GlobalResource.class, name = "global"),
        @JsonSubTypes.Type(value = LocalResource.class, name = "local"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public abstract class Resource extends AbstractPersistable {

    private int capacity;

    protected Resource() {
    }

    protected Resource(long id, int capacity) {
        super(id);
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public abstract boolean isRenewable();

}
