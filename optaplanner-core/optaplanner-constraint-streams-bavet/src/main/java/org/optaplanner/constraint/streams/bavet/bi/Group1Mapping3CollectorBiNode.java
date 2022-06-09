package org.optaplanner.constraint.streams.bavet.bi;

import static org.optaplanner.constraint.streams.bavet.bi.Group0Mapping3CollectorBiNode.mergeCollectors;

import java.util.function.BiFunction;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.util.Triple;

final class Group1Mapping3CollectorBiNode<OldA, OldB, A, B, C, D, ResultContainerB_, ResultContainerC_, ResultContainerD_>
        extends AbstractGroupBiNode<OldA, OldB, QuadTuple<A, B, C, D>, A, Object, Triple<B, C, D>> {

    private final BiFunction<OldA, OldB, A> groupKeyMapping;
    private final int outputStoreSize;

    public Group1Mapping3CollectorBiNode(BiFunction<OldA, OldB, A> groupKeyMapping, int groupStoreIndex,
            BiConstraintCollector<OldA, OldB, ResultContainerB_, B> collectorB,
            BiConstraintCollector<OldA, OldB, ResultContainerC_, C> collectorC,
            BiConstraintCollector<OldA, OldB, ResultContainerD_, D> collectorD,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorB, collectorC, collectorD), nextNodesTupleLifecycle);
        this.groupKeyMapping = groupKeyMapping;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected A createGroupKey(BiTuple<OldA, OldB> tuple) {
        return groupKeyMapping.apply(tuple.factA, tuple.factB);
    }

    @Override
    protected QuadTuple<A, B, C, D> createOutTuple(A a) {
        return new QuadTuple<>(a, null, null, null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(QuadTuple<A, B, C, D> outTuple, Triple<B, C, D> result) {
        outTuple.factB = result.getA();
        outTuple.factC = result.getB();
        outTuple.factD = result.getC();
    }

    @Override
    public String toString() {
        return "GroupBiNode 1+3";
    }

}
