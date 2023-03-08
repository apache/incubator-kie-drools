package org.optaplanner.constraint.streams.bavet.bi;

import static org.optaplanner.constraint.streams.bavet.bi.Group1Mapping0CollectorBiNode.createGroupKey;

import java.util.function.BiFunction;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.config.solver.EnvironmentMode;

final class Group1Mapping1CollectorBiNode<OldA, OldB, A, B, ResultContainer_>
        extends AbstractGroupBiNode<OldA, OldB, BiTuple<A, B>, BiTupleImpl<A, B>, A, ResultContainer_, B> {

    private final int outputStoreSize;

    public Group1Mapping1CollectorBiNode(BiFunction<OldA, OldB, A> groupKeyMapping, int groupStoreIndex, int undoStoreIndex,
            BiConstraintCollector<OldA, OldB, ResultContainer_, B> collector,
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
