package org.drools.core.phreak;

import org.drools.core.common.Memory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.spi.PropagationContext;

public interface TupleEntry {

    LeftTuple getLeftTuple();

    RightTuple getRightTuple();

    PropagationContext getPropagationContext();

    Memory getNodeMemory();

    int getPropagationType();

    TupleEntry getNext();
    void setNext(TupleEntry next);
}