package org.drools.core.phreak.actions;

public abstract class AbstractPartitionedPropagationEntry extends AbstractPropagationEntry {
    protected final int partition;

    protected AbstractPartitionedPropagationEntry(int partition) {
        this.partition = partition;
    }

    protected boolean isMainPartition() {
        return partition == 0;
    }
}
