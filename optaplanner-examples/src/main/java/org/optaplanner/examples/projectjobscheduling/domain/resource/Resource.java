package org.optaplanner.examples.projectjobscheduling.domain.resource;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamInclude;

@XStreamAlias("PjsResource")
@XStreamInclude({
        GlobalResource.class,
        LocalResource.class
})
public abstract class Resource extends AbstractPersistable {

    private int capacity;

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
