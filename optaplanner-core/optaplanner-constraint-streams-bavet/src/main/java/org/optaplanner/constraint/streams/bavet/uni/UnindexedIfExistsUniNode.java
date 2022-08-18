package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.BiPredicate;

import org.optaplanner.constraint.streams.bavet.common.AbstractUnindexedIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;

final class UnindexedIfExistsUniNode<A, B> extends AbstractUnindexedIfExistsNode<UniTuple<A>, B> {

    private final BiPredicate<A, B> filtering;

    public UnindexedIfExistsUniNode(boolean shouldExist, TupleLifecycle<UniTuple<A>> nextNodesTupleLifecycle,
            BiPredicate<A, B> filtering) {
        super(shouldExist, nextNodesTupleLifecycle, filtering != null);
        this.filtering = filtering;
    }

    @Override
    protected boolean testFiltering(UniTuple<A> leftTuple, UniTuple<B> rightTuple) {
        return filtering.test(leftTuple.getFactA(), rightTuple.getFactA());
    }

    @Override
    public String toString() {
        return "IfExistsUniWithUniNode";
    }

}
