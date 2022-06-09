package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;

final class Group1Mapping1CollectorQuadNode<OldA, OldB, OldC, OldD, A, B, ResultContainer_>
        extends AbstractGroupQuadNode<OldA, OldB, OldC, OldD, BiTuple<A, B>, A, ResultContainer_, B> {

    private final QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMapping;
    private final int outputStoreSize;

    public Group1Mapping1CollectorQuadNode(QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMapping, int groupStoreIndex,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainer_, B> collector,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, collector, nextNodesTupleLifecycle);
        this.groupKeyMapping = groupKeyMapping;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected A createGroupKey(QuadTuple<OldA, OldB, OldC, OldD> tuple) {
        return groupKeyMapping.apply(tuple.factA, tuple.factB, tuple.factC, tuple.factD);
    }

    @Override
    protected BiTuple<A, B> createOutTuple(A a) {
        return new BiTuple<>(a, null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(BiTuple<A, B> outTuple, B b) {
        outTuple.factB = b;
    }

    @Override
    public String toString() {
        return "GroupQuadNode 1+1";
    }

}
