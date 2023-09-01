package org.drools.metric.phreak;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakFromNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.metric.util.MetricLogUtils;

public class PhreakFromNodeMetric extends PhreakFromNode {

    @Override
    public void doNode(FromNode fromNode,
                       FromMemory fm,
                       LeftTupleSink sink,
                       ReteEvaluator reteEvaluator,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        try {
            MetricLogUtils.getInstance().startMetrics(fromNode);

            super.doNode(fromNode, fm, sink, reteEvaluator, srcLeftTuples, trgLeftTuples, stagedLeftTuples);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
