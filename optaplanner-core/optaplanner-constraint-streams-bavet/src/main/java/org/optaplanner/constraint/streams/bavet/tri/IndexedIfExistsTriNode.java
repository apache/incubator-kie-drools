package org.optaplanner.constraint.streams.bavet.tri;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIndexedIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.ExistsCounter;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.TriFunction;

final class IndexedIfExistsTriNode<A, B, C, D> extends AbstractIndexedIfExistsNode<TriTuple<A, B, C>, D> {

    private final TriFunction<A, B, C, IndexProperties> mappingABC;
    private final QuadPredicate<A, B, C, D> filtering;

    public IndexedIfExistsTriNode(boolean shouldExist,
            TriFunction<A, B, C, IndexProperties> mappingABC, Function<D, IndexProperties> mappingD,
            int inputStoreIndexLeftProperties, int inputStoreIndexLeftCounterEntry,
            int inputStoreIndexRightProperties, int inputStoreIndexRightEntry,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle,
            Indexer<ExistsCounter<TriTuple<A, B, C>>> indexerABC, Indexer<UniTuple<D>> indexerD) {
        this(shouldExist, mappingABC, mappingD,
                inputStoreIndexLeftProperties, inputStoreIndexLeftCounterEntry, -1,
                inputStoreIndexRightProperties, inputStoreIndexRightEntry, -1,
                nextNodesTupleLifecycle, indexerABC, indexerD,
                null);
    }

    public IndexedIfExistsTriNode(boolean shouldExist,
            TriFunction<A, B, C, IndexProperties> mappingABC, Function<D, IndexProperties> mappingD,
            int inputStoreIndexLeftProperties, int inputStoreIndexLeftCounterEntry, int inputStoreIndexLeftTrackerList,
            int inputStoreIndexRightProperties, int inputStoreIndexRightEntry, int inputStoreIndexRightTrackerList,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle,
            Indexer<ExistsCounter<TriTuple<A, B, C>>> indexerABC, Indexer<UniTuple<D>> indexerD,
            QuadPredicate<A, B, C, D> filtering) {
        super(shouldExist, mappingD,
                inputStoreIndexLeftProperties, inputStoreIndexLeftCounterEntry, inputStoreIndexLeftTrackerList,
                inputStoreIndexRightProperties, inputStoreIndexRightEntry, inputStoreIndexRightTrackerList,
                nextNodesTupleLifecycle, indexerABC, indexerD,
                filtering != null);
        this.mappingABC = mappingABC;
        this.filtering = filtering;
    }

    @Override
    protected IndexProperties createIndexProperties(TriTuple<A, B, C> leftTuple) {
        return mappingABC.apply(leftTuple.getFactA(), leftTuple.getFactB(), leftTuple.getFactC());
    }

    @Override
    protected boolean testFiltering(TriTuple<A, B, C> leftTuple, UniTuple<D> rightTuple) {
        return filtering.test(leftTuple.getFactA(), leftTuple.getFactB(), leftTuple.getFactC(), rightTuple.getFactA());
    }

}
