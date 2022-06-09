package org.optaplanner.constraint.streams.bavet.quad;

import static org.optaplanner.constraint.streams.bavet.quad.Group0Mapping2CollectorQuadNode.mergeCollectors;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.util.Pair;

final class Group2Mapping2CollectorQuadNode<OldA, OldB, OldC, OldD, A, B, C, D, ResultContainerC_, ResultContainerD_>
        extends AbstractGroupQuadNode<OldA, OldB, OldC, OldD, QuadTuple<A, B, C, D>, Pair<A, B>, Object, Pair<C, D>> {

    private final QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMappingA;
    private final QuadFunction<OldA, OldB, OldC, OldD, B> groupKeyMappingB;
    private final int outputStoreSize;

    public Group2Mapping2CollectorQuadNode(QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMappingA,
            QuadFunction<OldA, OldB, OldC, OldD, B> groupKeyMappingB, int groupStoreIndex,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerC_, C> collectorC,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerD_, D> collectorD,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorC, collectorD), nextNodesTupleLifecycle);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Pair<A, B> createGroupKey(QuadTuple<OldA, OldB, OldC, OldD> tuple) {
        OldA oldA = tuple.factA;
        OldB oldB = tuple.factB;
        OldC oldC = tuple.factC;
        OldD oldD = tuple.factD;
        A a = groupKeyMappingA.apply(oldA, oldB, oldC, oldD);
        B b = groupKeyMappingB.apply(oldA, oldB, oldC, oldD);
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
        return "GroupQuadNode 2+2";
    }

}
