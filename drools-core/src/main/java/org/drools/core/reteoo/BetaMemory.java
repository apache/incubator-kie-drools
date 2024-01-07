package org.drools.core.reteoo;

import org.drools.core.util.index.TupleList;

public interface BetaMemory<C> {
    TupleMemory getRightTupleMemory();

    TupleMemory getLeftTupleMemory();

    C getContext();

    void reset();
}
