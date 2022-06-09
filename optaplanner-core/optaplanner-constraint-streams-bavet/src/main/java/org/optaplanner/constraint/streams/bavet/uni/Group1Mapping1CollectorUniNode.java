package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

final class Group1Mapping1CollectorUniNode<OldA, A, B, ResultContainer_>
        extends AbstractGroupUniNode<OldA, BiTuple<A, B>, A, ResultContainer_, B> {

    private final Function<OldA, A> groupKeyMapping;
    private final int outputStoreSize;

    public Group1Mapping1CollectorUniNode(Function<OldA, A> groupKeyMapping, int groupStoreIndex,
            UniConstraintCollector<OldA, ResultContainer_, B> collector,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, collector, nextNodesTupleLifecycle);
        this.groupKeyMapping = groupKeyMapping;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected A createGroupKey(UniTuple<OldA> tuple) {
        return groupKeyMapping.apply(tuple.factA);
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
        return "GroupUniNode 1+1";
    }

}
