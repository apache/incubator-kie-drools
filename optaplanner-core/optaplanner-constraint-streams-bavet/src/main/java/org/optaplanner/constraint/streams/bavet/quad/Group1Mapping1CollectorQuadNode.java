package org.optaplanner.constraint.streams.bavet.quad;

import static org.optaplanner.constraint.streams.bavet.quad.Group1Mapping0CollectorQuadNode.createGroupKey;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.bi.BiTupleImpl;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.config.solver.EnvironmentMode;

final class Group1Mapping1CollectorQuadNode<OldA, OldB, OldC, OldD, A, B, ResultContainer_>
        extends AbstractGroupQuadNode<OldA, OldB, OldC, OldD, BiTuple<A, B>, BiTupleImpl<A, B>, A, ResultContainer_, B> {

    private final int outputStoreSize;

    public Group1Mapping1CollectorQuadNode(QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMapping, int groupStoreIndex,
            int undoStoreIndex, QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainer_, B> collector,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle, int outputStoreSize, EnvironmentMode environmentMode) {
        super(groupStoreIndex, undoStoreIndex, tuple -> createGroupKey(groupKeyMapping, tuple), collector,
                nextNodesTupleLifecycle, environmentMode);
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected BiTupleImpl<A, B> createOutTuple(A a) {
        return new BiTupleImpl<>(a, null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(BiTupleImpl<A, B> outTuple, B b) {
        outTuple.factB = b;
    }

}
