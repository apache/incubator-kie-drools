package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.util.Triple;

final class Group0Mapping3CollectorTriNode<OldA, OldB, OldC, A, B, C, ResultContainerA_, ResultContainerB_, ResultContainerC_>
        extends AbstractGroupTriNode<OldA, OldB, OldC, TriTuple<A, B, C>, String, Object, Triple<A, B, C>> {

    private static final String NO_GROUP_KEY = "NO_GROUP";

    private final int outputStoreSize;

    public Group0Mapping3CollectorTriNode(int groupStoreIndex,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerA_, A> collectorA,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerB_, B> collectorB,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerC_, C> collectorC,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorA, collectorB, collectorC), nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    static <OldA, OldB, OldC, A, B, C, ResultContainerA_, ResultContainerB_, ResultContainerC_>
            TriConstraintCollector<OldA, OldB, OldC, Object, Triple<A, B, C>> mergeCollectors(
                    TriConstraintCollector<OldA, OldB, OldC, ResultContainerA_, A> collectorA,
                    TriConstraintCollector<OldA, OldB, OldC, ResultContainerB_, B> collectorB,
                    TriConstraintCollector<OldA, OldB, OldC, ResultContainerC_, C> collectorC) {
        return (TriConstraintCollector<OldA, OldB, OldC, Object, Triple<A, B, C>>) ConstraintCollectors.compose(collectorA,
                collectorB, collectorC, Triple::of);
    }

    @Override
    protected String createGroupKey(TriTuple<OldA, OldB, OldC> tuple) {
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
        return "GroupTriNode 0+3";
    }

}
