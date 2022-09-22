package org.optaplanner.constraint.streams.bavet.bi;

import org.optaplanner.constraint.streams.bavet.common.AbstractUnindexedIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.TriPredicate;

final class UnindexedIfExistsBiNode<A, B, C> extends AbstractUnindexedIfExistsNode<BiTuple<A, B>, C> {

    private final TriPredicate<A, B, C> filtering;

    public UnindexedIfExistsBiNode(boolean shouldExist,
            int inputStoreIndexLeftCounterEntry, int inputStoreIndexRightEntry,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle) {
        this(shouldExist,
                inputStoreIndexLeftCounterEntry, -1,
                inputStoreIndexRightEntry, -1,
                nextNodesTupleLifecycle, null);
    }

    public UnindexedIfExistsBiNode(boolean shouldExist,
            int inputStoreIndexLeftCounterEntry, int inputStoreIndexLeftTrackerList,
            int inputStoreIndexRightEntry, int inputStoreIndexRightTrackerList,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle,
            TriPredicate<A, B, C> filtering) {
        super(shouldExist,
                inputStoreIndexLeftCounterEntry, inputStoreIndexLeftTrackerList,
                inputStoreIndexRightEntry, inputStoreIndexRightTrackerList,
                nextNodesTupleLifecycle, filtering != null);
        this.filtering = filtering;
    }

    @Override
    protected boolean testFiltering(BiTuple<A, B> leftTuple, UniTuple<C> rightTuple) {
        return filtering.test(leftTuple.getFactA(), leftTuple.getFactB(), rightTuple.getFactA());
    }

}
