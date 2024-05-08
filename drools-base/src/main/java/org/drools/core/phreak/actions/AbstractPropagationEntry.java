package org.drools.core.phreak.actions;

import org.drools.base.base.ValueResolver;
import org.drools.core.phreak.PropagationEntry;

public abstract class AbstractPropagationEntry<T extends ValueResolver> implements PropagationEntry<T> {
    protected PropagationEntry next;

    public void setNext(PropagationEntry next) {
        this.next = next;
    }

    public PropagationEntry getNext() {
        return next;
    }

    @Override
    public boolean requiresImmediateFlushing() {
        return false;
    }

    @Override
    public boolean isCalledFromRHS() {
        return false;
    }

    @Override
    public boolean isPartitionSplittable() {
        return false;
    }

    @Override
    public boolean defersExpiration() {
        return false;
    }

    @Override
    public PropagationEntry getSplitForPartition(int partitionNr) {
        throw new UnsupportedOperationException();
    }
}
