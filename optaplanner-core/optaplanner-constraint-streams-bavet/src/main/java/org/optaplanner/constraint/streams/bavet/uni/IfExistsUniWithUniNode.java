package org.optaplanner.constraint.streams.bavet.uni;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;

final class IfExistsUniWithUniNode<A, B> extends AbstractIfExistsNode<UniTuple<A>, B> {

    private final Function<A, IndexProperties> mappingA;
    private final BiPredicate<A, B> filtering;

    public IfExistsUniWithUniNode(boolean shouldExist,
            Function<A, IndexProperties> mappingA, Function<B, IndexProperties> mappingB,
            int inputStoreIndexA, int inputStoreIndexB,
            TupleLifecycle<UniTuple<A>> nextNodesTupleLifecycle,
            Indexer<UniTuple<A>, Counter<UniTuple<A>>> indexerA,
            Indexer<UniTuple<B>, Set<Counter<UniTuple<A>>>> indexerB,
            BiPredicate<A, B> filtering) {
        super(shouldExist, mappingB, inputStoreIndexA, inputStoreIndexB, nextNodesTupleLifecycle, indexerA, indexerB);
        this.mappingA = mappingA;
        this.filtering = filtering;
    }

    @Override
    protected IndexProperties createIndexProperties(UniTuple<A> leftTuple) {
        return mappingA.apply(leftTuple.getFactA());
    }

    @Override
    protected boolean isFiltering() {
        return filtering != null;
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
