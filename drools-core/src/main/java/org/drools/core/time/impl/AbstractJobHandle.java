package org.drools.core.time.impl;

import org.drools.base.time.JobHandle;
import org.drools.core.util.LinkedListNode;

public abstract class AbstractJobHandle<T extends AbstractJobHandle> implements JobHandle,
                                                   LinkedListNode<T> {

    private T previous;
    private T next;

    @Override
    public T getPrevious() {
        return previous;
    }

    @Override
    public void setPrevious(T previous) {
        this.previous = previous;
    }

    @Override
    public void nullPrevNext() {
        previous = null;
        next = null;
    }

    @Override
    public void setNext(T next) {
        this.next = next;
    }

    @Override
    public T getNext() {
        return next;
    }
}
