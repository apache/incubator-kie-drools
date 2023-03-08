package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.config.solver.EnvironmentMode;

final class Group1Mapping0CollectorUniNode<OldA, A>
        extends AbstractGroupUniNode<OldA, UniTuple<A>, UniTupleImpl<A>, A, Void, Void> {

    private final int outputStoreSize;

    public Group1Mapping0CollectorUniNode(Function<OldA, A> groupKeyMapping, int groupStoreIndex,
            TupleLifecycle<UniTuple<A>> nextNodesTupleLifecycle, int outputStoreSize, EnvironmentMode environmentMode) {
        super(groupStoreIndex, tuple -> createGroupKey(groupKeyMapping, tuple), nextNodesTupleLifecycle, environmentMode);
        this.outputStoreSize = outputStoreSize;
    }

    static <A, OldA> A createGroupKey(Function<OldA, A> groupKeyMapping, UniTuple<OldA> tuple) {
        return groupKeyMapping.apply(tuple.getFactA());
    }

    @Override
    protected UniTupleImpl<A> createOutTuple(A a) {
        return new UniTupleImpl<>(a, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(UniTupleImpl<A> aUniTuple, Void unused) {
        throw new IllegalStateException("Impossible state: collector is null.");
    }

}
