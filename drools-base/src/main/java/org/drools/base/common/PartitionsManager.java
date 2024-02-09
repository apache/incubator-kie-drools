/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.common;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import org.drools.util.ObjectPool;
import org.kie.internal.concurrent.ExecutorProviderFactory;

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
