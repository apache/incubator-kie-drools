package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;

final class Group0Mapping1CollectorQuadNode<OldA, OldB, OldC, OldD, A, ResultContainer_>
        extends AbstractGroupQuadNode<OldA, OldB, OldC, OldD, UniTuple<A>, String, ResultContainer_, A> {

    private static final String NO_GROUP_KEY = "NO_GROUP";

    private final int outputStoreSize;

    public Group0Mapping1CollectorQuadNode(int groupStoreIndex,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainer_, A> collector,
            TupleLifecycle<UniTuple<A>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, collector, nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected String createGroupKey(QuadTuple<OldA, OldB, OldC, OldD> tuple) {
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
        return "GroupQuadNode 0+1";
    }
}
