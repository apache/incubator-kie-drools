package org.optaplanner.constraint.streams.bavet.uni;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIndexedIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.ExistsCounter;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;

final class IndexedIfExistsUniNode<A, B> extends AbstractIndexedIfExistsNode<UniTuple<A>, B> {

    private final Function<A, IndexProperties> mappingA;
    private final BiPredicate<A, B> filtering;

    public IndexedIfExistsUniNode(boolean shouldExist,
            Function<A, IndexProperties> mappingA, Function<B, IndexProperties> mappingB,
            int inputStoreIndexA, int inputStoreIndexB,
            TupleLifecycle<UniTuple<A>> nextNodesTupleLifecycle,
            Indexer<UniTuple<A>, ExistsCounter<UniTuple<A>>> indexerA,
            Indexer<UniTuple<B>, Set<ExistsCounter<UniTuple<A>>>> indexerB,
            BiPredicate<A, B> filtering) {
        super(shouldExist, mappingB, inputStoreIndexA, inputStoreIndexB, nextNodesTupleLifecycle, indexerA, indexerB,
                filtering != null);
        this.mappingA = mappingA;
        this.filtering = filtering;
    }

    @Override
    protected IndexProperties createIndexProperties(UniTuple<A> leftTuple) {
        return mappingA.apply(leftTuple.getFactA());
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
