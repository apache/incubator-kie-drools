package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.util.Triple;

final class Group3Mapping1CollectorTriNode<OldA, OldB, OldC, A, B, C, D, ResultContainer_>
        extends AbstractGroupTriNode<OldA, OldB, OldC, QuadTuple<A, B, C, D>, Triple<A, B, C>, ResultContainer_, D> {

    private final TriFunction<OldA, OldB, OldC, A> groupKeyMappingA;
    private final TriFunction<OldA, OldB, OldC, B> groupKeyMappingB;
    private final TriFunction<OldA, OldB, OldC, C> groupKeyMappingC;
    private final int outputStoreSize;

    public Group3Mapping1CollectorTriNode(TriFunction<OldA, OldB, OldC, A> groupKeyMappingA,
            TriFunction<OldA, OldB, OldC, B> groupKeyMappingB, TriFunction<OldA, OldB, OldC, C> groupKeyMappingC,
            int groupStoreIndex, TriConstraintCollector<OldA, OldB, OldC, ResultContainer_, D> collector,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, collector, nextNodesTupleLifecycle);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.groupKeyMappingC = groupKeyMappingC;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Triple<A, B, C> createGroupKey(TriTuple<OldA, OldB, OldC> tuple) {
        OldA oldA = tuple.factA;
        OldB oldB = tuple.factB;
        OldC oldC = tuple.factC;
        A a = groupKeyMappingA.apply(oldA, oldB, oldC);
        B b = groupKeyMappingB.apply(oldA, oldB, oldC);
        C c = groupKeyMappingC.apply(oldA, oldB, oldC);
        return Triple.of(a, b, c);
    }

    @Override
    protected QuadTuple<A, B, C, D> createOutTuple(Triple<A, B, C> groupKey) {
        return new QuadTuple<>(groupKey.getA(), groupKey.getB(), groupKey.getC(), null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(QuadTuple<A, B, C, D> outTuple, D d) {
        outTuple.factD = d;
    }

    @Override
    public String toString() {
        return "GroupTriNode 3+1";
    }

}
