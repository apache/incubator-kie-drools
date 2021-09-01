/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.metric.phreak;

import org.drools.core.common.InternalAgenda;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakBranchNode;
import org.drools.core.phreak.RuleExecutor;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.ConditionalBranchNode.ConditionalBranchMemory;
import org.drools.metric.util.MetricLogUtils;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;

public class PhreakBranchNodeMetric extends PhreakBranchNode {

    @Override
    public void doNode(ConditionalBranchNode branchNode,
                       ConditionalBranchMemory cbm,
                       LeftTupleSink sink,
                       InternalAgenda agenda,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples,
                       RuleExecutor executor) {

        try {
            MetricLogUtils.getInstance().startMetrics(branchNode);

            super.doNode(branchNode, cbm, sink, agenda, srcLeftTuples, trgLeftTuples, stagedLeftTuples, executor);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
