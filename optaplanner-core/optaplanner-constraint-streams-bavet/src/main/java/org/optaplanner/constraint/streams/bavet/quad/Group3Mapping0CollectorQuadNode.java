package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.impl.util.Triple;

final class Group3Mapping0CollectorQuadNode<OldA, OldB, OldC, OldD, A, B, C>
        extends AbstractGroupQuadNode<OldA, OldB, OldC, OldD, TriTuple<A, B, C>, Triple<A, B, C>, Void, Void> {

    private final QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMappingA;
    private final QuadFunction<OldA, OldB, OldC, OldD, B> groupKeyMappingB;
    private final QuadFunction<OldA, OldB, OldC, OldD, C> groupKeyMappingC;
    private final int outputStoreSize;

    public Group3Mapping0CollectorQuadNode(QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMappingA,
            QuadFunction<OldA, OldB, OldC, OldD, B> groupKeyMappingB, QuadFunction<OldA, OldB, OldC, OldD, C> groupKeyMappingC,
            int groupStoreIndex,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, null, nextNodesTupleLifecycle);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.groupKeyMappingC = groupKeyMappingC;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Triple<A, B, C> createGroupKey(QuadTuple<OldA, OldB, OldC, OldD> tuple) {
        OldA oldA = tuple.factA;
        OldB oldB = tuple.factB;
        OldC oldC = tuple.factC;
        OldD oldD = tuple.factD;
        A a = groupKeyMappingA.apply(oldA, oldB, oldC, oldD);
        B b = groupKeyMappingB.apply(oldA, oldB, oldC, oldD);
        C c = groupKeyMappingC.apply(oldA, oldB, oldC, oldD);
        return Triple.of(a, b, c);
    }

    @Override
    protected TriTuple<A, B, C> createOutTuple(Triple<A, B, C> groupKey) {
        return new TriTuple<>(groupKey.getA(), groupKey.getB(), groupKey.getC(), outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(TriTuple<A, B, C> outTuple, Void unused) {
        throw new IllegalStateException("Impossible state: collector is null.");
    }

    @Override
    public String toString() {
        return "GroupQuadNode 3+0";
    }

}
