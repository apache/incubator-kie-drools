package org.optaplanner.constraint.streams.bavet.uni;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.config.solver.EnvironmentMode;

final class Group0Mapping1CollectorUniNode<OldA, A, ResultContainer_>
        extends AbstractGroupUniNode<OldA, UniTuple<A>, UniTupleImpl<A>, Void, ResultContainer_, A> {

    private final int outputStoreSize;

    public Group0Mapping1CollectorUniNode(int groupStoreIndex, int undoStoreIndex,
            UniConstraintCollector<OldA, ResultContainer_, A> collector,
            TupleLifecycle<UniTuple<A>> nextNodesTupleLifecycle, int outputStoreSize, EnvironmentMode environmentMode) {
        super(groupStoreIndex, undoStoreIndex, null, collector, nextNodesTupleLifecycle, environmentMode);
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected UniTupleImpl<A> createOutTuple(Void groupKey) {
        return new UniTupleImpl<>(null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(UniTupleImpl<A> outTuple, A a) {
        outTuple.factA = a;
    }

}
