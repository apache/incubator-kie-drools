package org.optaplanner.constraint.streams.bavet.bi;

import org.optaplanner.constraint.streams.bavet.common.AbstractUnindexedJoinNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

final class UnindexedJoinBiNode<A, B>
        extends AbstractUnindexedJoinNode<UniTuple<A>, B, BiTuple<A, B>, BiTupleImpl<A, B>> {

    private final int outputStoreSize;

    public UnindexedJoinBiNode(TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected BiTupleImpl<A, B> createOutTuple(UniTuple<A> leftTuple, UniTuple<B> rightTuple) {
        return new BiTupleImpl<>(leftTuple.getFactA(), rightTuple.getFactA(), outputStoreSize);
    }

    @Override
    protected void updateOutTupleLeft(BiTupleImpl<A, B> outTuple, UniTuple<A> leftTuple) {
        outTuple.factA = leftTuple.getFactA();
    }

    @Override
    protected void updateOutTupleRight(BiTupleImpl<A, B> outTuple, UniTuple<B> rightTuple) {
        outTuple.factB = rightTuple.getFactA();
    }

    @Override
    public String toString() {
        return "JoinBiNode";
    }

}
