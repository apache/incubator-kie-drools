package org.optaplanner.constraint.streams.bavet.bi;

import java.util.function.BiFunction;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.core.impl.util.Quadruple;

final class Group4Mapping0CollectorBiNode<OldA, OldB, A, B, C, D>
        extends AbstractGroupBiNode<OldA, OldB, QuadTuple<A, B, C, D>, Quadruple<A, B, C, D>, Void, Void> {

    private final BiFunction<OldA, OldB, A> groupKeyMappingA;
    private final BiFunction<OldA, OldB, B> groupKeyMappingB;
    private final BiFunction<OldA, OldB, C> groupKeyMappingC;
    private final BiFunction<OldA, OldB, D> groupKeyMappingD;
    private final int outputStoreSize;

    public Group4Mapping0CollectorBiNode(BiFunction<OldA, OldB, A> groupKeyMappingA, BiFunction<OldA, OldB, B> groupKeyMappingB,
            BiFunction<OldA, OldB, C> groupKeyMappingC, BiFunction<OldA, OldB, D> groupKeyMappingD, int groupStoreIndex,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, null, nextNodesTupleLifecycle);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.groupKeyMappingC = groupKeyMappingC;
        this.groupKeyMappingD = groupKeyMappingD;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Quadruple<A, B, C, D> createGroupKey(BiTuple<OldA, OldB> tuple) {
        OldA oldA = tuple.factA;
        OldB oldB = tuple.factB;
        A a = groupKeyMappingA.apply(oldA, oldB);
        B b = groupKeyMappingB.apply(oldA, oldB);
        C c = groupKeyMappingC.apply(oldA, oldB);
        D d = groupKeyMappingD.apply(oldA, oldB);
        return Quadruple.of(a, b, c, d);
    }

    @Override
    protected QuadTuple<A, B, C, D> createOutTuple(Quadruple<A, B, C, D> groupKey) {
        return new QuadTuple<>(groupKey.getA(), groupKey.getB(), groupKey.getC(), groupKey.getD(), outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(QuadTuple<A, B, C, D> outTuple, Void unused) {
        throw new IllegalStateException("Impossible state: collector is null.");
    }

    @Override
    public String toString() {
        return "GroupBiNode 4+0";
    }

}
