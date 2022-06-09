package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;

final class Group1Mapping1CollectorTriNode<OldA, OldB, OldC, A, B, ResultContainer_>
        extends AbstractGroupTriNode<OldA, OldB, OldC, BiTuple<A, B>, A, ResultContainer_, B> {

    private final TriFunction<OldA, OldB, OldC, A> groupKeyMapping;
    private final int outputStoreSize;

    public Group1Mapping1CollectorTriNode(TriFunction<OldA, OldB, OldC, A> groupKeyMapping, int groupStoreIndex,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainer_, B> collector,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, collector, nextNodesTupleLifecycle);
        this.groupKeyMapping = groupKeyMapping;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected A createGroupKey(TriTuple<OldA, OldB, OldC> tuple) {
        return groupKeyMapping.apply(tuple.factA, tuple.factB, tuple.factC);
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
        return "GroupTriNode 1+1";
    }

}
