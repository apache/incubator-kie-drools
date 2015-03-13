package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.ReactiveFromNode.ReactiveFromMemory;

public class PhreakReactiveFromNode extends PhreakFromNode {
    public void doNode(ReactiveFromNode fromNode,
                       ReactiveFromMemory fm,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       LeftTupleSets srcLeftTuples,
                       LeftTupleSets trgLeftTuples,
                       LeftTupleSets stagedLeftTuples) {

        super.doNode(fromNode, fm, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        trgLeftTuples.addAll(fm.getStagedLeftTuples().takeAll());
    }
}
