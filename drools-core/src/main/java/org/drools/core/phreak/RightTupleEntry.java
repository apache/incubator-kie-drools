package org.drools.core.phreak;

import org.drools.core.common.Memory;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.PropagationContext;

public class RightTupleEntry implements TupleEntry {

    private final RightTuple         rt;
    private final PropagationContext pctx;
    private final Memory             nodeMemory;
    private final int                propagationType;

    private TupleEntry next = null;

    public RightTupleEntry(RightTuple rt, PropagationContext pctx, Memory nodeMemory, int propagationType) {
        this.rt = rt;
        this.pctx = pctx;
        this.nodeMemory = nodeMemory;
        this.propagationType = propagationType;
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

    public int getPropagationType() {
        return propagationType;
    }

    @Override
    public String toString() {

        return "RightTupleEntry{" +
               "rt=" + rt +
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
