package org.drools.core.phreak;

import org.drools.core.common.Memory;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.PropagationContext;

public class LeftTupleEntry implements TupleEntry {
    private LeftTuple          lt;
    private PropagationContext pctx;
    private Memory             nodeMemory;

    public LeftTupleEntry(LeftTuple lt, PropagationContext pctx, Memory nodeMemory) {
        this.lt = lt;
        this.pctx = pctx;
        this.nodeMemory = nodeMemory;
    }

    public LeftTuple getLeftTuple() {
        return lt;
    }

    public RightTuple getRightTuple() {
        return null;
    }

    public PropagationContext getPropagationContext() {
        return pctx;
    }

    public Memory getNodeMemory() {
        return nodeMemory;
    }

    @Override
    public String toString() {
        return "LeftTupleEntry{" +
               "lt=" + lt +
               ", pctx=" + PhreakPropagationContext.intEnumToString(pctx) +
               ", nodeMemory=" + nodeMemory +
               '}';
    }
}
