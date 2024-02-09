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
        return this == obj || (obj instanceof RuleBasePartitionId o && id == o.id);
    }

    @Override
    public String toString() {
        return "Partition(" + (id == 0 ? "MAIN" : id) + ")";
    }
}
