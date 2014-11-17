package org.drools.core.time.impl;

import org.drools.core.time.JobHandle;

public abstract class AbstractJobHandle implements JobHandle {

    private JobHandle previous;
    private JobHandle next;

    @Override
    public JobHandle getPrevious() {
        return previous;
    }

    @Override
    public void setPrevious(JobHandle previous) {
        this.previous = previous;
    }

    @Override
    public void nullPrevNext() {
        previous = null;
        next = null;
    }

    @Override
    public void setNext(JobHandle next) {
        this.next = next;
    }

    @Override
    public JobHandle getNext() {
        return next;
    }
}
