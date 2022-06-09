package org.optaplanner.constraint.streams.bavet.bi;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.TriPredicate;

final class IfExistsBiWithUniNode<A, B, C> extends AbstractIfExistsNode<BiTuple<A, B>, C> {

    private final BiFunction<A, B, IndexProperties> mappingAB;
    private final TriPredicate<A, B, C> filtering;

    public IfExistsBiWithUniNode(boolean shouldExist,
            BiFunction<A, B, IndexProperties> mappingAB, Function<C, IndexProperties> mappingC,
            int inputStoreIndexAB, int inputStoreIndexC,
            TupleLifecycle<BiTuple<A, B>> tupleLifecycle,
            Indexer<BiTuple<A, B>, Counter<BiTuple<A, B>>> indexerAB,
            Indexer<UniTuple<C>, Set<Counter<BiTuple<A, B>>>> indexerC,
            TriPredicate<A, B, C> filtering) {
        super(shouldExist, mappingC, inputStoreIndexAB, inputStoreIndexC, tupleLifecycle, indexerAB, indexerC);
        this.mappingAB = mappingAB;
        this.filtering = filtering;
    }

    @Override
    protected IndexProperties createIndexProperties(BiTuple<A, B> abBiTuple) {
        return mappingAB.apply(abBiTuple.factA, abBiTuple.factB);
    }

    @Override
    protected boolean isFiltering() {
        return filtering != null;
    }

    @Override
    protected boolean isFiltered(BiTuple<A, B> abBiTuple, UniTuple<C> rightTuple) {
        return filtering.test(abBiTuple.factA, abBiTuple.factB, rightTuple.factA);
    }

    @Override
    public String toString() {
        return "IfExistsBiWithUniNode";
    }

}
