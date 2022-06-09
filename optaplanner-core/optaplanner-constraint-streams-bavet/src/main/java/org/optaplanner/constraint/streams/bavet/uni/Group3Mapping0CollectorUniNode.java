package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.impl.util.Triple;

final class Group3Mapping0CollectorUniNode<OldA, A, B, C>
        extends AbstractGroupUniNode<OldA, TriTuple<A, B, C>, Triple<A, B, C>, Void, Void> {

    private final Function<OldA, A> groupKeyMappingA;
    private final Function<OldA, B> groupKeyMappingB;
    private final Function<OldA, C> groupKeyMappingC;
    private final int outputStoreSize;

    public Group3Mapping0CollectorUniNode(Function<OldA, A> groupKeyMappingA, Function<OldA, B> groupKeyMappingB,
            Function<OldA, C> groupKeyMappingC, int groupStoreIndex,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, null, nextNodesTupleLifecycle);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.groupKeyMappingC = groupKeyMappingC;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Triple<A, B, C> createGroupKey(UniTuple<OldA> tuple) {
        OldA oldA = tuple.factA;
        A a = groupKeyMappingA.apply(oldA);
        B b = groupKeyMappingB.apply(oldA);
        C c = groupKeyMappingC.apply(oldA);
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
        return "GroupUniNode 3+0";
    }

}
