package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.AbstractTuple;

public final class TriTupleImpl<A, B, C> extends AbstractTuple implements TriTuple<A, B, C> {

    // Only a tuple's origin node may modify a fact.
    public A factA;
    public B factB;
    public C factC;

    public TriTupleImpl(A factA, B factB, C factC, int storeSize) {
        super(storeSize);
        this.factA = factA;
        this.factB = factB;
        this.factC = factC;
    }

    @Override
    public A getFactA() {
        return factA;
    }

    @Override
    public B getFactB() {
        return factB;
    }

    @Override
    public C getFactC() {
        return factC;
    }

    @Override
    public String toString() {
        return "{" + factA + ", " + factB + ", " + factC + "}";
    }

}
