package org.optaplanner.constraint.streams.bavet.uni;

import static org.optaplanner.constraint.streams.bavet.uni.Group1Mapping0CollectorUniNode.createGroupKey;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.bi.BiTupleImpl;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.config.solver.EnvironmentMode;

final class Group1Mapping1CollectorUniNode<OldA, A, B, ResultContainer_>
        extends AbstractGroupUniNode<OldA, BiTuple<A, B>, BiTupleImpl<A, B>, A, ResultContainer_, B> {

    private final int outputStoreSize;

    public Group1Mapping1CollectorUniNode(Function<OldA, A> groupKeyMapping, int groupStoreIndex, int undoStoreIndex,
            UniConstraintCollector<OldA, ResultContainer_, B> collector,
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
