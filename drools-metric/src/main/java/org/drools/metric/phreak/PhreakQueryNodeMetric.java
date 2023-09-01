package org.drools.metric.phreak;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakQueryNode;
import org.drools.core.phreak.StackEntry;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.metric.util.MetricLogUtils;

public class PhreakQueryNodeMetric extends PhreakQueryNode {

    @Override
    public void doNode(QueryElementNode queryNode,
                       QueryElementNodeMemory qmem,
                       StackEntry stackEntry,
                       ReteEvaluator reteEvaluator,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        try {
            MetricLogUtils.getInstance().startMetrics(queryNode);

            super.doNode(queryNode, qmem, stackEntry, reteEvaluator, srcLeftTuples, trgLeftTuples, stagedLeftTuples);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
