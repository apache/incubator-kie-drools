package org.drools.metric.phreak;

import org.drools.core.common.ActivationsManager;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakQueryTerminalNode;
import org.drools.core.phreak.StackEntry;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.util.LinkedList;
import org.drools.metric.util.MetricLogUtils;

public class PhreakQueryTerminalNodeMetric extends PhreakQueryTerminalNode {

    @Override
    public void doNode(QueryTerminalNode qtnNode,
                       ActivationsManager activationsManager,
                       TupleSets<LeftTuple> srcLeftTuples,
                       LinkedList<StackEntry> stack) {

        try {
            MetricLogUtils.getInstance().startMetrics(qtnNode);

            super.doNode(qtnNode, activationsManager, srcLeftTuples, stack);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
