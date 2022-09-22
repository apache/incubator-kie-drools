package org.optaplanner.constraint.streams.bavet.bi;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIndexedIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.ExistsCounter;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.TriPredicate;

final class IndexedIfExistsBiNode<A, B, C> extends AbstractIndexedIfExistsNode<BiTuple<A, B>, C> {

    private final BiFunction<A, B, IndexProperties> mappingAB;
    private final TriPredicate<A, B, C> filtering;

    public IndexedIfExistsBiNode(boolean shouldExist,
            BiFunction<A, B, IndexProperties> mappingAB, Function<C, IndexProperties> mappingC,
            int inputStoreIndexLeftProperties, int inputStoreIndexLeftCounterEntry,
            int inputStoreIndexRightProperties, int inputStoreIndexRightEntry,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle,
            Indexer<ExistsCounter<BiTuple<A, B>>> indexerAB, Indexer<UniTuple<C>> indexerC) {
        this(shouldExist, mappingAB, mappingC,
                inputStoreIndexLeftProperties, inputStoreIndexLeftCounterEntry, -1,
                inputStoreIndexRightProperties, inputStoreIndexRightEntry, -1,
                nextNodesTupleLifecycle, indexerAB, indexerC,
                null);
    }

    public IndexedIfExistsBiNode(boolean shouldExist,
            BiFunction<A, B, IndexProperties> mappingAB, Function<C, IndexProperties> mappingC,
            int inputStoreIndexLeftProperties, int inputStoreIndexLeftCounterEntry, int inputStoreIndexLeftTrackerList,
            int inputStoreIndexRightProperties, int inputStoreIndexRightEntry, int inputStoreIndexRightTrackerList,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle,
            Indexer<ExistsCounter<BiTuple<A, B>>> indexerAB, Indexer<UniTuple<C>> indexerC,
            TriPredicate<A, B, C> filtering) {
        super(shouldExist, mappingC,
                inputStoreIndexLeftProperties, inputStoreIndexLeftCounterEntry, inputStoreIndexLeftTrackerList,
                inputStoreIndexRightProperties, inputStoreIndexRightEntry, inputStoreIndexRightTrackerList,
                nextNodesTupleLifecycle, indexerAB, indexerC,
                filtering != null);
        this.mappingAB = mappingAB;
        this.filtering = filtering;
    }

    @Override
    protected IndexProperties createIndexProperties(BiTuple<A, B> leftTuple) {
        return mappingAB.apply(leftTuple.getFactA(), leftTuple.getFactB());
    }

    @Override
    protected boolean testFiltering(BiTuple<A, B> leftTuple, UniTuple<C> rightTuple) {
        return filtering.test(leftTuple.getFactA(), leftTuple.getFactB(), rightTuple.getFactA());
    }

}
