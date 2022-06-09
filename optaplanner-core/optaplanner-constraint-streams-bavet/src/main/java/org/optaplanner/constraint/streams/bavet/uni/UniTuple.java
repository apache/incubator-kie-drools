package org.optaplanner.constraint.streams.bavet.uni;

import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.common.Tuple;

public final class UniTuple<A> implements Tuple {

    // Only a tuple's origin node may modify a fact.
    public A factA;

    public final Object[] store;

    public BavetTupleState state;

    public UniTuple(A factA, int storeSize) {
        this.factA = factA;
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
        return "{" + factA + "}";
    }

}
