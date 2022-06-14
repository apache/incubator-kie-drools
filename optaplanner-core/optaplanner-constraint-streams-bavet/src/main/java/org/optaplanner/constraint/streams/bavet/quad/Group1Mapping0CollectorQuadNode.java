package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.QuadFunction;

final class Group1Mapping0CollectorQuadNode<OldA, OldB, OldC, OldD, A>
        extends AbstractGroupQuadNode<OldA, OldB, OldC, OldD, UniTuple<A>, A, Void, Void> {

    private final int outputStoreSize;

    public Group1Mapping0CollectorQuadNode(QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMapping, int groupStoreIndex,
            TupleLifecycle<UniTuple<A>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, tuple -> createGroupKey(groupKeyMapping, tuple), null, nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    static <A, OldA, OldB, OldC, OldD> A createGroupKey(QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMapping,
            QuadTuple<OldA, OldB, OldC, OldD> tuple) {
        return groupKeyMapping.apply(tuple.factA, tuple.factB, tuple.factC, tuple.factD);
    }

    @Override
    protected UniTuple<A> createOutTuple(A a) {
        return new UniTuple<>(a, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(UniTuple<A> aUniTuple, Void unused) {
        throw new IllegalStateException("Impossible state: collector is null.");
    }

    @Override
    public String toString() {
        return "GroupQuadNode 1+0";
    }

}
