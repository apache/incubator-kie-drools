package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.AbstractUnindexedIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.QuadPredicate;

final class UnindexedIfExistsTriNode<A, B, C, D> extends AbstractUnindexedIfExistsNode<TriTuple<A, B, C>, D> {

    private final QuadPredicate<A, B, C, D> filtering;

    public UnindexedIfExistsTriNode(boolean shouldExist, TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle,
            QuadPredicate<A, B, C, D> filtering) {
        super(shouldExist, nextNodesTupleLifecycle, filtering != null);
        this.filtering = filtering;
    }

    @Override
    protected boolean testFiltering(TriTuple<A, B, C> leftTuple, UniTuple<D> rightTuple) {
        return filtering.test(leftTuple.getFactA(), leftTuple.getFactB(), leftTuple.getFactC(), rightTuple.getFactA());
    }

    @Override
    public String toString() {
        return "IfExistsTriWithUniNode";
    }

}
