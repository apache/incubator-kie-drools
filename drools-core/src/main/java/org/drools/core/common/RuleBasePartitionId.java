/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.common;

import org.kie.api.concurrent.KieExecutors;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class to identify RuleBase partitions
 */
public final class RuleBasePartitionId implements Serializable {

    private static final long serialVersionUID = 510l;

    public static final int PARALLEL_PARTITIONS_NUMBER = KieExecutors.Pool.SIZE;

    public static final RuleBasePartitionId MAIN_PARTITION = new RuleBasePartitionId( 0 );

    private static final AtomicInteger PARTITION_COUNTER = new AtomicInteger( 1 );

    private final int id;

    private RuleBasePartitionId( int id ) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getParallelEvaluationSlot() {
        return id % PARALLEL_PARTITIONS_NUMBER;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof RuleBasePartitionId && id == ((RuleBasePartitionId)obj).id);
    }

    public String toString() {
        return "Partition(" + (id == 0 ? "MAIN" : id) + ")";
    }

    public static RuleBasePartitionId createPartition() {
        return new RuleBasePartitionId( PARTITION_COUNTER.getAndIncrement() );
    }
}
