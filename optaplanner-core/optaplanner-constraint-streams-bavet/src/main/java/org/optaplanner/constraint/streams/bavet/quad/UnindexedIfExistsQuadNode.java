package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.AbstractUnindexedIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.PentaPredicate;

final class UnindexedIfExistsQuadNode<A, B, C, D, E> extends AbstractUnindexedIfExistsNode<QuadTuple<A, B, C, D>, E> {

    private final PentaPredicate<A, B, C, D, E> filtering;

    public UnindexedIfExistsQuadNode(boolean shouldExist,
            int inputStoreIndexLeftCounterEntry, int inputStoreIndexRightEntry,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle) {
        this(shouldExist,
                inputStoreIndexLeftCounterEntry, -1,
                inputStoreIndexRightEntry, -1,
                nextNodesTupleLifecycle, null);
    }

    public UnindexedIfExistsQuadNode(boolean shouldExist,
            int inputStoreIndexLeftCounterEntry, int inputStoreIndexLeftTrackerList,
            int inputStoreIndexRightEntry, int inputStoreIndexRightTrackerList,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle,
            PentaPredicate<A, B, C, D, E> filtering) {
        super(shouldExist,
                inputStoreIndexLeftCounterEntry, inputStoreIndexLeftTrackerList,
                inputStoreIndexRightEntry, inputStoreIndexRightTrackerList,
                nextNodesTupleLifecycle, filtering != null);
        this.filtering = filtering;
    }

    @Override
    protected boolean testFiltering(QuadTuple<A, B, C, D> leftTuple, UniTuple<E> rightTuple) {
        return filtering.test(leftTuple.getFactA(), leftTuple.getFactB(), leftTuple.getFactC(), leftTuple.getFactD(),
                rightTuple.getFactA());
    }

}
