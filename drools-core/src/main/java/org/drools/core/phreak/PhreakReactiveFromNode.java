package org.drools.core.phreak;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.ReactiveFromNode.ReactiveFromMemory;

public class PhreakReactiveFromNode extends PhreakFromNode {
    public void doNode(ReactiveFromNode fromNode,
                       ReactiveFromMemory fm,
                       LeftTupleSink sink,
                       ReteEvaluator reteEvaluator,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        super.doNode(fromNode, fm, sink, reteEvaluator, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        trgLeftTuples.addAll(fm.getStagedLeftTuples().takeAll());
    }
}
