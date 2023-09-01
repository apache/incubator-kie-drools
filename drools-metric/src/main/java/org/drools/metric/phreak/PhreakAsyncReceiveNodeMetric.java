package org.drools.metric.phreak;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakAsyncReceiveNode;
import org.drools.core.reteoo.AsyncReceiveNode;
import org.drools.core.reteoo.AsyncReceiveNode.AsyncReceiveMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.metric.util.MetricLogUtils;

public class PhreakAsyncReceiveNodeMetric extends PhreakAsyncReceiveNode {

    @Override
    public void doNode(AsyncReceiveNode node,
                       AsyncReceiveMemory memory,
                       LeftTupleSink sink,
                       ReteEvaluator reteEvaluator,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples) {

        try {
            MetricLogUtils.getInstance().startMetrics(node);

            super.doNode(node, memory, sink, reteEvaluator, srcLeftTuples, trgLeftTuples);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
