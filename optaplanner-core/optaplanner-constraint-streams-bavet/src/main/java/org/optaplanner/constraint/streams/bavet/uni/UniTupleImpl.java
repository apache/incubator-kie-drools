package org.optaplanner.constraint.streams.bavet.uni;

import org.optaplanner.constraint.streams.bavet.common.AbstractTuple;

public final class UniTupleImpl<A> extends AbstractTuple implements UniTuple<A> {

    // Only a tuple's origin node may modify a fact.
    public A factA;

    public UniTupleImpl(A factA, int storeSize) {
        super(storeSize);
        this.factA = factA;
    }

    @Override
    public A getFactA() {
        return factA;
    }

    @Override
    public String toString() {
        return "{" + factA + "}";
    }

}
