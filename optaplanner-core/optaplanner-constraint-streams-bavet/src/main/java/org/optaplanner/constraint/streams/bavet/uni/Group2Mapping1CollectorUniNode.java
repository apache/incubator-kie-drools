package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.util.Pair;

final class Group2Mapping1CollectorUniNode<OldA, A, B, C, ResultContainer_>
        extends AbstractGroupUniNode<OldA, TriTuple<A, B, C>, Pair<A, B>, ResultContainer_, C> {

    private final Function<OldA, A> groupKeyMappingA;
    private final Function<OldA, B> groupKeyMappingB;
    private final int outputStoreSize;

    public Group2Mapping1CollectorUniNode(Function<OldA, A> groupKeyMappingA, Function<OldA, B> groupKeyMappingB,
            int groupStoreIndex, UniConstraintCollector<OldA, ResultContainer_, C> collector,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, collector, nextNodesTupleLifecycle);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Pair<A, B> createGroupKey(UniTuple<OldA> tuple) {
        OldA oldA = tuple.factA;
        A a = groupKeyMappingA.apply(oldA);
        B b = groupKeyMappingB.apply(oldA);
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
        return "GroupUniNode 2+1";
    }

}
