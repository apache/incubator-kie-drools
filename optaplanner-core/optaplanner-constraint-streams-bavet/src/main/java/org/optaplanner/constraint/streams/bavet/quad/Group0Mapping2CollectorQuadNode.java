package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.util.Pair;

final class Group0Mapping2CollectorQuadNode<OldA, OldB, OldC, OldD, A, B, ResultContainerA_, ResultContainerB_>
        extends AbstractGroupQuadNode<OldA, OldB, OldC, OldD, BiTuple<A, B>, Void, Object, Pair<A, B>> {

    private final int outputStoreSize;

    public Group0Mapping2CollectorQuadNode(int groupStoreIndex,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerA_, A> collectorA,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerB_, B> collectorB,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, null, mergeCollectors(collectorA, collectorB), nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    static <OldA, OldB, OldC, OldD, A, B, ResultContainerA_, ResultContainerB_>
            QuadConstraintCollector<OldA, OldB, OldC, OldD, Object, Pair<A, B>> mergeCollectors(
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerA_, A> collectorA,
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerB_, B> collectorB) {
        return (QuadConstraintCollector<OldA, OldB, OldC, OldD, Object, Pair<A, B>>) ConstraintCollectors.compose(collectorA,
                collectorB, Pair::of);
    }

    @Override
    protected BiTuple<A, B> createOutTuple(Void groupKey) {
        return new BiTuple<>(null, null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(BiTuple<A, B> outTuple, Pair<A, B> result) {
        outTuple.factA = result.getKey();
        outTuple.factB = result.getValue();
    }

    @Override
    public String toString() {
        return "GroupQuadNode 0+2";
    }
}
