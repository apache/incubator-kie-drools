package org.drools.metric.phreak;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakJoinNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.metric.util.MetricLogUtils;

public class PhreakJoinNodeMetric extends PhreakJoinNode {

    @Override
    public void doNode(JoinNode joinNode,
                       LeftTupleSink sink,
                       BetaMemory bm,
                       ReteEvaluator reteEvaluator,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {
        try {
            MetricLogUtils.getInstance().startMetrics(joinNode);

            super.doNode(joinNode, sink, bm, reteEvaluator, srcLeftTuples, trgLeftTuples, stagedLeftTuples);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
