package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.util.Quadruple;

final class Group0Mapping4CollectorQuadNode<OldA, OldB, OldC, OldD, A, B, C, D, ResultContainerA_, ResultContainerB_, ResultContainerC_, ResultContainerD_>
        extends AbstractGroupQuadNode<OldA, OldB, OldC, OldD, QuadTuple<A, B, C, D>, Void, Object, Quadruple<A, B, C, D>> {

    private final int outputStoreSize;

    public Group0Mapping4CollectorQuadNode(int groupStoreIndex,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerA_, A> collectorA,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerB_, B> collectorB,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerC_, C> collectorC,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerD_, D> collectorD,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, null, mergeCollectors(collectorA, collectorB, collectorC, collectorD), nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    static <OldA, OldB, OldC, OldD, A, B, C, D, ResultContainerA_, ResultContainerB_, ResultContainerC_, ResultContainerD_>
            QuadConstraintCollector<OldA, OldB, OldC, OldD, Object, Quadruple<A, B, C, D>> mergeCollectors(
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerA_, A> collectorA,
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerB_, B> collectorB,
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerC_, C> collectorC,
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerD_, D> collectorD) {
        return (QuadConstraintCollector<OldA, OldB, OldC, OldD, Object, Quadruple<A, B, C, D>>) ConstraintCollectors.compose(
                collectorA, collectorB, collectorC, collectorD, Quadruple::of);
    }

    @Override
    protected QuadTuple<A, B, C, D> createOutTuple(Void groupKey) {
        return new QuadTuple<>(null, null, null, null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(QuadTuple<A, B, C, D> outTuple, Quadruple<A, B, C, D> result) {
        outTuple.factA = result.getA();
        outTuple.factB = result.getB();
        outTuple.factC = result.getC();
        outTuple.factD = result.getD();
    }

    @Override
    public String toString() {
        return "GroupQuadNode 0+4";
    }

}
