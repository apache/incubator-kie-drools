package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.bavet.uni.UniTupleImpl;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.config.solver.EnvironmentMode;

final class Group0Mapping1CollectorTriNode<OldA, OldB, OldC, A, ResultContainer_>
        extends AbstractGroupTriNode<OldA, OldB, OldC, UniTuple<A>, UniTupleImpl<A>, Void, ResultContainer_, A> {

    private final int outputStoreSize;

    public Group0Mapping1CollectorTriNode(int groupStoreIndex, int undoStoreIndex,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainer_, A> collector,
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
