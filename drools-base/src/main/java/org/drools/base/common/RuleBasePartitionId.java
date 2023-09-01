package org.drools.base.common;

/**
 * A class to identify RuleBase partitions
 */
public final class RuleBasePartitionId {

    public static final RuleBasePartitionId MAIN_PARTITION = new RuleBasePartitionId(null, 0);

    private final PartitionsManager partitionsManager;

    private final int id;

    public RuleBasePartitionId(PartitionsManager partitionsManager, int id ) {
        this.partitionsManager = partitionsManager;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getParallelEvaluationSlot() {
        return id % partitionsManager.getParallelEvaluationSlotsCount();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof RuleBasePartitionId && id == ((RuleBasePartitionId)obj).id);
    }

    @Override
    public String toString() {
        return "Partition(" + (id == 0 ? "MAIN" : id) + ")";
    }
}
