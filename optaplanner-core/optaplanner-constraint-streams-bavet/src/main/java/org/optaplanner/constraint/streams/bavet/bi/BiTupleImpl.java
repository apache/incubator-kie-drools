package org.optaplanner.constraint.streams.bavet.bi;

import org.optaplanner.constraint.streams.bavet.common.AbstractTuple;

public final class BiTupleImpl<A, B> extends AbstractTuple implements BiTuple<A, B> {

    // Only a tuple's origin node may modify a fact.
    public A factA;
    public B factB;

    public BiTupleImpl(A factA, B factB, int storeSize) {
        super(storeSize);
        this.factA = factA;
        this.factB = factB;
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
    public String toString() {
        return "{" + factA + ", " + factB + "}";
    }

}
