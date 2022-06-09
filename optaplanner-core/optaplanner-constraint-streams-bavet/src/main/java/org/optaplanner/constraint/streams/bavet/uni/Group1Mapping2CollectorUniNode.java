package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.util.Pair;

final class Group1Mapping2CollectorUniNode<OldA, A, B, C, ResultContainerB_, ResultContainerC_>
        extends AbstractGroupUniNode<OldA, TriTuple<A, B, C>, A, Object, Pair<B, C>> {

    private final Function<OldA, A> groupKeyMapping;
    private final int outputStoreSize;

    public Group1Mapping2CollectorUniNode(Function<OldA, A> groupKeyMapping, int groupStoreIndex,
            UniConstraintCollector<OldA, ResultContainerB_, B> collectorB,
            UniConstraintCollector<OldA, ResultContainerC_, C> collectorC,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, Group0Mapping2CollectorUniNode.mergeCollectors(collectorB, collectorC), nextNodesTupleLifecycle);
        this.groupKeyMapping = groupKeyMapping;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected A createGroupKey(UniTuple<OldA> tuple) {
        return groupKeyMapping.apply(tuple.factA);
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
        return "GroupUniNode 1+2";
    }

}
