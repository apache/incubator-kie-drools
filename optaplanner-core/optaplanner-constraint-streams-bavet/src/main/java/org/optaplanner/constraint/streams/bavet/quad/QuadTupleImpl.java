package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.AbstractTuple;

public final class QuadTupleImpl<A, B, C, D> extends AbstractTuple implements QuadTuple<A, B, C, D> {

    // Only a tuple's origin node may modify a fact.
    public A factA;
    public B factB;
    public C factC;
    public D factD;

    public QuadTupleImpl(A factA, B factB, C factC, D factD, int storeSize) {
        super(storeSize);
        this.factA = factA;
        this.factB = factB;
        this.factC = factC;
        this.factD = factD;
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
    public D getFactD() {
        return factD;
    }

    @Override
    public String toString() {
        return "{" + factA + ", " + factB + ", " + factC + ", " + factD + "}";
    }

}
