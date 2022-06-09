package org.optaplanner.constraint.streams.bavet.bi;

import java.util.function.BiFunction;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.util.Pair;

final class Group2Mapping1CollectorBiNode<OldA, OldB, A, B, C, ResultContainer_>
        extends AbstractGroupBiNode<OldA, OldB, TriTuple<A, B, C>, Pair<A, B>, ResultContainer_, C> {

    private final BiFunction<OldA, OldB, A> groupKeyMappingA;
    private final BiFunction<OldA, OldB, B> groupKeyMappingB;
    private final int outputStoreSize;

    public Group2Mapping1CollectorBiNode(BiFunction<OldA, OldB, A> groupKeyMappingA, BiFunction<OldA, OldB, B> groupKeyMappingB,
            int groupStoreIndex,
            BiConstraintCollector<OldA, OldB, ResultContainer_, C> collector,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, collector, nextNodesTupleLifecycle);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Pair<A, B> createGroupKey(BiTuple<OldA, OldB> tuple) {
        OldA oldA = tuple.factA;
        OldB oldB = tuple.factB;
        A a = groupKeyMappingA.apply(oldA, oldB);
        B b = groupKeyMappingB.apply(oldA, oldB);
        return Pair.of(a, b);
    }

    @Override
    protected TriTuple<A, B, C> createOutTuple(Pair<A, B> groupKey) {
        return new TriTuple<>(groupKey.getKey(), groupKey.getValue(), null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(TriTuple<A, B, C> outTuple, C c) {
        outTuple.factC = c;
    }

    @Override
    public String toString() {
        return "GroupBiNode 2+1";
    }

}
