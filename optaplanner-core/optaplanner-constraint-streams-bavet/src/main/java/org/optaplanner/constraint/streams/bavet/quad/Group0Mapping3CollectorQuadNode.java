package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.util.Triple;

final class Group0Mapping3CollectorQuadNode<OldA, OldB, OldC, OldD, A, B, C, ResultContainerA_, ResultContainerB_, ResultContainerC_>
        extends AbstractGroupQuadNode<OldA, OldB, OldC, OldD, TriTuple<A, B, C>, String, Object, Triple<A, B, C>> {

    private static final String NO_GROUP_KEY = "NO_GROUP";

    private final int outputStoreSize;

    public Group0Mapping3CollectorQuadNode(int groupStoreIndex,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerA_, A> collectorA,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerB_, B> collectorB,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerC_, C> collectorC,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorA, collectorB, collectorC), nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    static <OldA, OldB, OldC, OldD, A, B, C, ResultContainerA_, ResultContainerB_, ResultContainerC_>
            QuadConstraintCollector<OldA, OldB, OldC, OldD, Object, Triple<A, B, C>> mergeCollectors(
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerA_, A> collectorA,
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerB_, B> collectorB,
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerC_, C> collectorC) {
        return (QuadConstraintCollector<OldA, OldB, OldC, OldD, Object, Triple<A, B, C>>) ConstraintCollectors.compose(
                collectorA,
                collectorB, collectorC, Triple::of);
    }

    @Override
    protected String createGroupKey(QuadTuple<OldA, OldB, OldC, OldD> tuple) {
        return NO_GROUP_KEY;
    }

    @Override
    protected TriTuple<A, B, C> createOutTuple(String groupKey) {
        return new TriTuple<>(null, null, null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(TriTuple<A, B, C> outTuple, Triple<A, B, C> result) {
        outTuple.factA = result.getA();
        outTuple.factB = result.getB();
        outTuple.factC = result.getC();
    }

    @Override
    public String toString() {
        return "GroupQuadNode 0+3";
    }

}
