package org.drools.base.common;

import org.drools.util.ObjectPool;
import org.kie.internal.concurrent.ExecutorProviderFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class PartitionsManager {

    public static final int MIN_PARALLEL_THRESHOLD = 8;
    public static final int MAX_PARALLEL_THRESHOLD = MIN_PARALLEL_THRESHOLD * 4;

    private int partitionCounter = 0;

    private int parallelEvaluationSlotsCount = -1;

    public RuleBasePartitionId createNewPartitionId() {
        return new RuleBasePartitionId(this, ++partitionCounter);
    }

    public boolean hasParallelEvaluation() {
        return partitionCounter >= MIN_PARALLEL_THRESHOLD;
    }

    public int getParallelEvaluationSlotsCount() {
        return parallelEvaluationSlotsCount;
    }

    public void init() {
        this.parallelEvaluationSlotsCount = Math.min(partitionCounter, MAX_PARALLEL_THRESHOLD);
    }

    private static class ForkJoinPoolHolder {
        private static final ForkJoinPool RULES_EVALUATION_POOL = new ForkJoinPool(); // avoid common pool
    }

    public static void doOnForkJoinPool(Runnable task) {
        ForkJoinPoolHolder.RULES_EVALUATION_POOL.submit( task ).join();
    }

    public static <T> T doOnForkJoinPool(Callable<T> task) {
        return ForkJoinPoolHolder.RULES_EVALUATION_POOL.submit( task ).join();
    }

    private static class FireUntilHaltExecutorsPoolHolder {
        private static final ObjectPool<ExecutorService> POOL = ObjectPool.newLockFreePool( () -> ExecutorProviderFactory.getExecutorProvider().newFixedThreadPool(MAX_PARALLEL_THRESHOLD));
    }

    public static ExecutorService borrowFireUntilHaltExecutors() {
        return FireUntilHaltExecutorsPoolHolder.POOL.borrow();
    }

    public static void offerFireUntilHaltExecutors(ExecutorService executor) {
        FireUntilHaltExecutorsPoolHolder.POOL.offer(executor);
    }
}
