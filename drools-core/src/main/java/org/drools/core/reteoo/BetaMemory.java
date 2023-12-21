package org.drools.core.reteoo;

public interface BetaMemory<C> {
    TupleMemory getRightTupleMemory();

    TupleMemory getLeftTupleMemory();

    C getContext();

    void reset();
}
