package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.AbstractUnindexedIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.PentaPredicate;

final class UnindexedIfExistsQuadNode<A, B, C, D, E> extends AbstractUnindexedIfExistsNode<QuadTuple<A, B, C, D>, E> {

    private final PentaPredicate<A, B, C, D, E> filtering;

    public UnindexedIfExistsQuadNode(boolean shouldExist,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle,
            PentaPredicate<A, B, C, D, E> filtering) {
        super(shouldExist, nextNodesTupleLifecycle, filtering != null);
        this.filtering = filtering;
    }

    @Override
    protected boolean testFiltering(QuadTuple<A, B, C, D> leftTuple, UniTuple<E> rightTuple) {
        return filtering.test(leftTuple.getFactA(), leftTuple.getFactB(), leftTuple.getFactC(), leftTuple.getFactD(),
                rightTuple.getFactA());
    }

    @Override
    public String toString() {
        return "IfExistsQuadWithUniNode";
    }

}
