package org.optaplanner.constraint.streams.bavet.tri;

import java.util.Set;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.TriFunction;

final class IfExistsTriWithUniNode<A, B, C, D> extends AbstractIfExistsNode<TriTuple<A, B, C>, D> {

    private final TriFunction<A, B, C, IndexProperties> mappingABC;
    private final QuadPredicate<A, B, C, D> filtering;

    public IfExistsTriWithUniNode(boolean shouldExist,
            TriFunction<A, B, C, IndexProperties> mappingABC, Function<D, IndexProperties> mappingD,
            int inputStoreIndexABC, int inputStoreIndexD,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle,
            Indexer<TriTuple<A, B, C>, Counter<TriTuple<A, B, C>>> indexerABC,
            Indexer<UniTuple<D>, Set<Counter<TriTuple<A, B, C>>>> indexerD,
            QuadPredicate<A, B, C, D> filtering) {
        super(shouldExist, mappingD, inputStoreIndexABC, inputStoreIndexD, nextNodesTupleLifecycle, indexerABC,
                indexerD);
        this.mappingABC = mappingABC;
        this.filtering = filtering;
    }

    @Override
    protected IndexProperties createIndexProperties(TriTuple<A, B, C> abcTriTuple) {
        return mappingABC.apply(abcTriTuple.factA, abcTriTuple.factB, abcTriTuple.factC);
    }

    @Override
    protected boolean isFiltering() {
        return filtering != null;
    }

    @Override
    protected boolean isFiltered(TriTuple<A, B, C> abcTriTuple, UniTuple<D> rightTuple) {
        return filtering.test(abcTriTuple.factA, abcTriTuple.factB, abcTriTuple.factC, rightTuple.factA);
    }

    @Override
    public String toString() {
        return "IfExistsTriWithUniNode";
    }

}
