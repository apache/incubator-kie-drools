package org.drools.core.phreak.actions;

import org.drools.base.base.ValueResolver;
import org.drools.base.phreak.actions.AbstractPropagationEntry;

public abstract class AbstractPartitionedPropagationEntry<T extends ValueResolver> extends AbstractPropagationEntry<T> {
    protected final int partition;

    protected AbstractPartitionedPropagationEntry(int partition) {
        this.partition = partition;
    }

    protected boolean isMainPartition() {
        return partition == 0;
    }
}
