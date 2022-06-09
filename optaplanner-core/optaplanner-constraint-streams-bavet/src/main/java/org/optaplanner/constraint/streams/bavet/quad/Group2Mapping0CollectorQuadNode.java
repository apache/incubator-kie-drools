package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.impl.util.Pair;

final class Group2Mapping0CollectorQuadNode<OldA, OldB, OldC, OldD, A, B>
        extends AbstractGroupQuadNode<OldA, OldB, OldC, OldD, BiTuple<A, B>, Pair<A, B>, Void, Void> {

    private final QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMappingA;
    private final QuadFunction<OldA, OldB, OldC, OldD, B> groupKeyMappingB;
    private final int outputStoreSize;

    public Group2Mapping0CollectorQuadNode(QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMappingA,
            QuadFunction<OldA, OldB, OldC, OldD, B> groupKeyMappingB, int groupStoreIndex,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, null, nextNodesTupleLifecycle);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Pair<A, B> createGroupKey(QuadTuple<OldA, OldB, OldC, OldD> tuple) {
        OldA oldA = tuple.factA;
        OldB oldB = tuple.factB;
        OldC oldC = tuple.factC;
        OldD oldD = tuple.factD;
        A a = groupKeyMappingA.apply(oldA, oldB, oldC, oldD);
        B b = groupKeyMappingB.apply(oldA, oldB, oldC, oldD);
        return Pair.of(a, b);
    }

    @Override
    protected BiTuple<A, B> createOutTuple(Pair<A, B> groupKey) {
        return new BiTuple<>(groupKey.getKey(), groupKey.getValue(), outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(BiTuple<A, B> outTuple, Void unused) {
        throw new IllegalStateException("Impossible state: collector is null.");
    }

    @Override
    public String toString() {
        return "GroupQuadNode 2+0";
    }

}
