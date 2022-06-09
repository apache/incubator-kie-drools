package org.optaplanner.core.impl.solver.thread;

import org.optaplanner.core.impl.partitionedsearch.PartitionedSearchPhase;

public enum ChildThreadType {
    /**
     * Used by {@link PartitionedSearchPhase}.
     */
    PART_THREAD,
    /**
     * Used by multithreaded incremental solving.
     */
    MOVE_THREAD;
}
