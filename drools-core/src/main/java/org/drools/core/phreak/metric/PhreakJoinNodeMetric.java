package org.drools.core.phreak.metric;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakJoinNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.util.PerfLogUtils;

public class PhreakJoinNodeMetric extends PhreakJoinNode {

    @Override
    public void doNode(JoinNode joinNode,
                       LeftTupleSink sink,
                       BetaMemory bm,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {
        try {
            PerfLogUtils.getInstance().startMetrics(joinNode);

            super.doNode(joinNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);

        } finally {
            PerfLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
