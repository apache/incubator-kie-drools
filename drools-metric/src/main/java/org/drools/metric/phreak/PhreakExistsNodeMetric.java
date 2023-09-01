package org.drools.metric.phreak;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakExistsNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.metric.util.MetricLogUtils;

public class PhreakExistsNodeMetric extends PhreakExistsNode {

    @Override
    public void doNode(ExistsNode existsNode,
                       LeftTupleSink sink,
                       BetaMemory bm,
                       ReteEvaluator reteEvaluator,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        try {
            MetricLogUtils.getInstance().startMetrics(existsNode);

            super.doNode(existsNode, sink, bm, reteEvaluator, srcLeftTuples, trgLeftTuples, stagedLeftTuples);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
