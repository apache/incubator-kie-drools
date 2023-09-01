package org.drools.metric.phreak;

import org.drools.core.common.ActivationsManager;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakBranchNode;
import org.drools.core.phreak.RuleExecutor;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.ConditionalBranchNode.ConditionalBranchMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.metric.util.MetricLogUtils;

public class PhreakBranchNodeMetric extends PhreakBranchNode {

    @Override
    public void doNode(ConditionalBranchNode branchNode,
                       ConditionalBranchMemory cbm,
                       LeftTupleSink sink,
                       ActivationsManager activationsManager,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples,
                       RuleExecutor executor) {

        try {
            MetricLogUtils.getInstance().startMetrics(branchNode);

            super.doNode(branchNode, cbm, sink, activationsManager, srcLeftTuples, trgLeftTuples, stagedLeftTuples, executor);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
