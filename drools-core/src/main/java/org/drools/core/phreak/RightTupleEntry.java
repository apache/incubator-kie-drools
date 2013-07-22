package org.drools.core.phreak;

import org.drools.core.common.Memory;
import org.drools.core.common.RetePropagationContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.PropagationContext;

public class RightTupleEntry implements TupleEntry {
    private RightTuple         rt;
    private PropagationContext pctx;
    private Memory             nodeMemory;

    public RightTupleEntry(RightTuple rt, PropagationContext pctx, Memory nodeMemory) {
        this.rt = rt;
        this.pctx = pctx;
        this.nodeMemory = nodeMemory;

    }

    public LeftTuple getLeftTuple() {
        return null;
    }

    public RightTuple getRightTuple() {
        return rt;
    }

    public PropagationContext getPropagationContext() {
        return pctx;
    }

    public Memory getNodeMemory() {
        return nodeMemory;
    }

    @Override
    public String toString() {

        return "RightTupleEntry{" +
               "rt=" + rt +
               ", pctx=" + RetePropagationContext.intEnumToString(pctx) +
               ", nodeMemory=" + nodeMemory +
               '}';
    }
}
