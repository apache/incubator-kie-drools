package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.impl.util.Pair;

final class Group2Mapping0CollectorTriNode<OldA, OldB, OldC, A, B>
        extends AbstractGroupTriNode<OldA, OldB, OldC, BiTuple<A, B>, Pair<A, B>, Void, Void> {

    private final TriFunction<OldA, OldB, OldC, A> groupKeyMappingA;
    private final TriFunction<OldA, OldB, OldC, B> groupKeyMappingB;
    private final int outputStoreSize;

    public Group2Mapping0CollectorTriNode(TriFunction<OldA, OldB, OldC, A> groupKeyMappingA,
            TriFunction<OldA, OldB, OldC, B> groupKeyMappingB, int groupStoreIndex,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, null, nextNodesTupleLifecycle);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Pair<A, B> createGroupKey(TriTuple<OldA, OldB, OldC> tuple) {
        OldA oldA = tuple.factA;
        OldB oldB = tuple.factB;
        OldC oldC = tuple.factC;
        A a = groupKeyMappingA.apply(oldA, oldB, oldC);
        B b = groupKeyMappingB.apply(oldA, oldB, oldC);
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
        return "GroupTriNode 2+0";
    }

}
