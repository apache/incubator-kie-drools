package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.util.Pair;

final class Group1Mapping2CollectorTriNode<OldA, OldB, OldC, A, B, C, ResultContainerB_, ResultContainerC_>
        extends AbstractGroupTriNode<OldA, OldB, OldC, TriTuple<A, B, C>, A, Object, Pair<B, C>> {

    private final TriFunction<OldA, OldB, OldC, A> groupKeyMapping;
    private final int outputStoreSize;

    public Group1Mapping2CollectorTriNode(TriFunction<OldA, OldB, OldC, A> groupKeyMapping, int groupStoreIndex,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerB_, B> collectorB,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerC_, C> collectorC,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, Group0Mapping2CollectorTriNode.mergeCollectors(collectorB, collectorC), nextNodesTupleLifecycle);
        this.groupKeyMapping = groupKeyMapping;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected A createGroupKey(TriTuple<OldA, OldB, OldC> tuple) {
        OldA oldA = tuple.factA;
        OldB oldB = tuple.factB;
        OldC oldC = tuple.factC;
        return groupKeyMapping.apply(oldA, oldB, oldC);
    }

    @Override
    protected TriTuple<A, B, C> createOutTuple(A a) {
        return new TriTuple<>(a, null, null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(TriTuple<A, B, C> outTuple, Pair<B, C> result) {
        outTuple.factB = result.getKey();
        outTuple.factC = result.getValue();
    }

    @Override
    public String toString() {
        return "GroupTriNode 1+2";
    }

}
