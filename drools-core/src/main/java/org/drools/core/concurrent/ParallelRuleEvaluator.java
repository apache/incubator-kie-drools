/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.concurrent;

import org.drools.base.common.RuleBasePartitionId;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.phreak.RuleAgendaItem;
import org.kie.api.runtime.rule.AgendaFilter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.drools.base.common.PartitionsManager.MIN_PARALLEL_THRESHOLD;

public class ParallelRuleEvaluator extends AbstractRuleEvaluator {

    public ParallelRuleEvaluator(ActivationsManager activationsManager ) {
        super(activationsManager);
    }

    @Override
    public int evaluateAndFire( AgendaFilter filter,
                                int fireCount,
                                int fireLimit,
                                InternalAgendaGroup group ) {

        Map<RuleBasePartitionId, List<RuleAgendaItem>> partitionedActivations = group.getActivations().stream().collect(Collectors.groupingBy(RuleAgendaItem::getPartition));
        if (partitionedActivations.size() < MIN_PARALLEL_THRESHOLD) {
            RuleAgendaItem item = group.peek();
            return item != null ? internalEvaluateAndFire( filter, fireCount, fireLimit, item ) : 0;
        }

        partitionedActivations.values().parallelStream().forEach( items -> items.forEach( item -> item.getRuleExecutor().evaluateNetworkIfDirty(activationsManager) ) );
        RuleAgendaItem item = nextActivation(group);
        while ((fireLimit < 0 || fireCount < fireLimit) && item != null) {
            item.getRuleExecutor().evaluateNetworkIfDirty(activationsManager);
            fireCount += item.getRuleExecutor().fire(activationsManager, filter, fireCount, fireLimit);
            item = nextActivation(group);
        }
        return fireCount;
    }
}
