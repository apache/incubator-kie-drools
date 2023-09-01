package org.drools.metric.phreak;

import org.drools.core.common.ActivationsManager;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakTimerNode;
import org.drools.core.reteoo.LeftTuple;
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
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        try {
            MetricLogUtils.getInstance().startMetrics(timerNode);

            super.doNode(timerNode, tm, pmem, smem, sink, activationsManager, srcLeftTuples, trgLeftTuples, stagedLeftTuples);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
