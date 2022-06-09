package org.optaplanner.constraint.streams.bavet.uni;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

final class Group0Mapping1CollectorUniNode<OldA, A, ResultContainer_>
        extends AbstractGroupUniNode<OldA, UniTuple<A>, String, ResultContainer_, A> {

    private static final String NO_GROUP_KEY = "NO_GROUP";

    private final int outputStoreSize;

    public Group0Mapping1CollectorUniNode(int groupStoreIndex,
            UniConstraintCollector<OldA, ResultContainer_, A> collector,
            TupleLifecycle<UniTuple<A>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, collector, nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected String createGroupKey(UniTuple<OldA> tuple) {
        return NO_GROUP_KEY;
    }

    @Override
    protected UniTuple<A> createOutTuple(String groupKey) {
        return new UniTuple<>(null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(UniTuple<A> outTuple, A a) {
        outTuple.factA = a;
    }

    @Override
    public String toString() {
        return "GroupUniNode 0+1";
    }
}
