package org.drools.core.phreak;

import org.drools.core.common.Memory;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.PropagationContext;

public class LeftTupleEntry implements TupleEntry {
    private final LeftTuple          lt;
    private final PropagationContext pctx;
    private final Memory             nodeMemory;
    private final int                propagationType;

    private TupleEntry next;

    public LeftTupleEntry(LeftTuple lt, PropagationContext pctx, Memory nodeMemory, int propagationType) {
        this.lt = lt;
        this.pctx = pctx;
        this.nodeMemory = nodeMemory;
        this.propagationType = propagationType;
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

    public int getPropagationType() {
        return propagationType;
    }


    @Override
    public String toString() {
        return "LeftTupleEntry{" +
               "lt=" + lt +
               ", pctx=" + PhreakPropagationContext.intEnumToString(pctx) +
               ", nodeMemory=" + nodeMemory +
               '}';
    }

    public TupleEntry getNext() {
        return next;
    }

    public void setNext(TupleEntry next) {
        this.next = next;
    }
}
