package org.drools.base.common;

import java.util.concurrent.atomic.AtomicInteger;

public class PartitionsManager {

    private static final int MIN_PARALLEL_THRESHOLD = Runtime.getRuntime().availableProcessors();
    private static final int MAX_PARALLEL_THRESHOLD = Runtime.getRuntime().availableProcessors();

    private final AtomicInteger partitionCounter = new AtomicInteger( 0 );

    private int parallelEvaluationSlotsCount = -1;

    public RuleBasePartitionId createNewPartitionId() {
        return new RuleBasePartitionId(this, partitionCounter.incrementAndGet());
    }

    public int getPartitionsCount() {
        return partitionCounter.get();
    }

    public boolean hasParallelEvaluation() {
        return getPartitionsCount() >= MIN_PARALLEL_THRESHOLD;
    }

    public int getParallelEvaluationSlotsCount() {
        return parallelEvaluationSlotsCount;
    }

    public void init() {
        this.parallelEvaluationSlotsCount = Math.min(getPartitionsCount(), MAX_PARALLEL_THRESHOLD);
    }
}
