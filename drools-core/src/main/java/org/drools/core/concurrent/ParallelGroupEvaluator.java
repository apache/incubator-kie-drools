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
package org.drools.core.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.base.common.RuleBasePartitionId;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.phreak.RuleAgendaItem;

import static org.drools.base.common.PartitionsManager.MIN_PARALLEL_THRESHOLD;
import static org.drools.base.common.PartitionsManager.doOnForkJoinPool;

public class ParallelGroupEvaluator extends AbstractGroupEvaluator {

    public ParallelGroupEvaluator(ActivationsManager activationsManager ) {
        super(activationsManager);
    }

    protected void startEvaluation(InternalAgendaGroup group) {
        super.startEvaluation(group);
        parallelPreEvaluation(group);
    }

    private void parallelPreEvaluation(InternalAgendaGroup group) {
        Collection<RuleAgendaItem> activations = group.getActivations();
        if (activations.size() < MIN_PARALLEL_THRESHOLD) {
            // avoid parallel evaluation if there aren't enough activations ...
            return;
        }

        Map<RuleBasePartitionId, List<RuleAgendaItem>> partitionedActivations = activations.stream().collect(Collectors.groupingBy(RuleAgendaItem::getPartition));
        if (partitionedActivations.size() < MIN_PARALLEL_THRESHOLD) {
            // ... or partitions
            return;
        }

        // This will evaluate all the RuleAgendaItem (grouped by partitions) in parallel, also resetting
        // their dirty flag. After this AbstractGroupEvaluator#evaluateAndFire loop will attempt re-evaluating
        // those items again, but finding them not dirty it won't have any performance impact allowing a direct firing.
        doOnForkJoinPool(() ->
                partitionedActivations.values().parallelStream()
                        .forEach( items -> items
                                .forEach( item -> item.getRuleExecutor().evaluateNetworkIfDirty(activationsManager) ) )
        );
    }
}
