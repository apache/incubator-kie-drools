package org.optaplanner.constraint.streams.bavet.uni;

import static org.optaplanner.constraint.streams.bavet.uni.Group0Mapping2CollectorUniNode.mergeCollectors;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.util.Pair;

final class Group2Mapping2CollectorUniNode<OldA, A, B, C, D, ResultContainerC_, ResultContainerD_>
        extends AbstractGroupUniNode<OldA, QuadTuple<A, B, C, D>, Pair<A, B>, Object, Pair<C, D>> {

    private final Function<OldA, A> groupKeyMappingA;
    private final Function<OldA, B> groupKeyMappingB;
    private final int outputStoreSize;

    public Group2Mapping2CollectorUniNode(Function<OldA, A> groupKeyMappingA, Function<OldA, B> groupKeyMappingB,
            int groupStoreIndex,
            UniConstraintCollector<OldA, ResultContainerC_, C> collectorC,
            UniConstraintCollector<OldA, ResultContainerD_, D> collectorD,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorC, collectorD), nextNodesTupleLifecycle);
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
    protected QuadTuple<A, B, C, D> createOutTuple(Pair<A, B> groupKey) {
        return new QuadTuple<>(groupKey.getKey(), groupKey.getValue(), null, null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(QuadTuple<A, B, C, D> outTuple, Pair<C, D> result) {
        outTuple.factC = result.getKey();
        outTuple.factD = result.getValue();
    }

    @Override
    public String toString() {
        return "GroupUniNode 2+2";
    }

}
