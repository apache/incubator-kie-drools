package org.optaplanner.examples.projectjobscheduling.domain.resource;

public class GlobalResource extends Resource {

    public GlobalResource() { // For Jackson.
    }

    public GlobalResource(long id, int capacity) {
        super(id, capacity);
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public boolean isRenewable() {
        return true;
    }

}
