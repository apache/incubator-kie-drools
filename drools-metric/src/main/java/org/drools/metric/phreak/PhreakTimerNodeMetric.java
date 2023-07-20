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

import org.drools.core.common.ActivationsManager;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakTimerNode;
import org.drools.core.reteoo.AbstractLeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.reteoo.TimerNode.TimerNodeMemory;
import org.drools.metric.util.MetricLogUtils;

public class PhreakTimerNodeMetric extends PhreakTimerNode {

    @Override
    public void doNode(TimerNode timerNode,
                       TimerNodeMemory tm,
                       PathMemory pmem,
                       SegmentMemory smem,
                       LeftTupleSink sink,
                       ActivationsManager activationsManager,
                       TupleSets<AbstractLeftTuple> srcLeftTuples,
                       TupleSets<AbstractLeftTuple> trgLeftTuples,
                       TupleSets<AbstractLeftTuple> stagedLeftTuples) {

        try {
            MetricLogUtils.getInstance().startMetrics(timerNode);

            super.doNode(timerNode, tm, pmem, smem, sink, activationsManager, srcLeftTuples, trgLeftTuples, stagedLeftTuples);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
