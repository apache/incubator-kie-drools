package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.common.Tuple;

public final class QuadTuple<A, B, C, D> implements Tuple {

    // Only a tuple's origin node may modify a fact.
    public A factA;
    public B factB;
    public C factC;
    public D factD;

    public final Object[] store;

    public BavetTupleState state;

    public QuadTuple(A factA, B factB, C factC, D factD, int storeSize) {
        this.factA = factA;
        this.factB = factB;
        this.factC = factC;
        this.factD = factD;
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
        return "{" + factA + ", " + factB + ", " + factC + ", " + factD + "}";
    }
}
