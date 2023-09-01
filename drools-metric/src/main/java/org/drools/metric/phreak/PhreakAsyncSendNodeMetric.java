package org.drools.metric.phreak;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakAsyncSendNode;
import org.drools.core.reteoo.AsyncSendNode;
import org.drools.core.reteoo.AsyncSendNode.AsyncSendMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.metric.util.MetricLogUtils;

public class PhreakAsyncSendNodeMetric extends PhreakAsyncSendNode {

    @Override
    public void doNode(AsyncSendNode node,
                       AsyncSendMemory memory,
                       ReteEvaluator reteEvaluator,
                       TupleSets<LeftTuple> srcLeftTuples) {

        try {
            MetricLogUtils.getInstance().startMetrics(node);

            super.doNode(node, memory, reteEvaluator, srcLeftTuples);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
