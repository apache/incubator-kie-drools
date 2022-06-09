package org.optaplanner.constraint.streams.bavet.bi;

import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.common.Tuple;

public final class BiTuple<A, B> implements Tuple {

    // Only a tuple's origin node may modify a fact.
    public A factA;
    public B factB;

    public final Object[] store;

    public BavetTupleState state;

    public BiTuple(A factA, B factB, int storeSize) {
        this.factA = factA;
        this.factB = factB;
        store = (storeSize <= 0) ? null : new Object[storeSize];
    }

    @Override
    public BavetTupleState getState() {
        return state;
    }

    @Override
    public void setState(BavetTupleState state) {
        this.state = state;
    }

    @Override
    public Object[] getStore() {
        return store;
    }

    @Override
    public String toString() {
        return "{" + factA + ", " + factB + "}";
    }

}
