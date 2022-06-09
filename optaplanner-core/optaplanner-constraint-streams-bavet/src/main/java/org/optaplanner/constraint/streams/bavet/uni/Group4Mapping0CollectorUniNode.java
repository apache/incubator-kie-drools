package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.core.impl.util.Quadruple;

final class Group4Mapping0CollectorUniNode<OldA, A, B, C, D>
        extends AbstractGroupUniNode<OldA, QuadTuple<A, B, C, D>, Quadruple<A, B, C, D>, Void, Void> {

    private final Function<OldA, A> groupKeyMappingA;
    private final Function<OldA, B> groupKeyMappingB;
    private final Function<OldA, C> groupKeyMappingC;
    private final Function<OldA, D> groupKeyMappingD;
    private final int outputStoreSize;

    public Group4Mapping0CollectorUniNode(Function<OldA, A> groupKeyMappingA, Function<OldA, B> groupKeyMappingB,
            Function<OldA, C> groupKeyMappingC, Function<OldA, D> groupKeyMappingD, int groupStoreIndex,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, null, nextNodesTupleLifecycle);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.groupKeyMappingC = groupKeyMappingC;
        this.groupKeyMappingD = groupKeyMappingD;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Quadruple<A, B, C, D> createGroupKey(UniTuple<OldA> tuple) {
        OldA oldA = tuple.factA;
        A a = groupKeyMappingA.apply(oldA);
        B b = groupKeyMappingB.apply(oldA);
        C c = groupKeyMappingC.apply(oldA);
        D d = groupKeyMappingD.apply(oldA);
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
        return "GroupUniNode 4+0";
    }

}
