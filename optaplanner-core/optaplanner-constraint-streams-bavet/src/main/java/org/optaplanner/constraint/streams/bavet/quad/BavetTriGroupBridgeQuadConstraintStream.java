package org.optaplanner.constraint.streams.bavet.quad;

import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.AbstractGroupNode;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.tri.BavetGroupTriConstraintStream;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

final class BavetTriGroupBridgeQuadConstraintStream<Solution_, A, B, C, D, NewA, NewB, NewC>
        extends BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    protected final BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> parent;
    protected BavetGroupTriConstraintStream<Solution_, NewA, NewB, NewC> groupStream;
    private final QuadGroupNodeConstructor<A, B, C, D, TriTuple<NewA, NewB, NewC>> nodeConstructor;

    public BavetTriGroupBridgeQuadConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadGroupNodeConstructor<A, B, C, D, TriTuple<NewA, NewB, NewC>> nodeConstructor) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        this.nodeConstructor = nodeConstructor;
    }

    @Override
    public boolean guaranteesDistinct() {
        return true;
    }

    public void setGroupStream(BavetGroupTriConstraintStream<Solution_, NewA, NewB, NewC> groupStream) {
        this.groupStream = groupStream;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parent.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        if (!childStreamList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") has an non-empty childStreamList (" + childStreamList + ") but it's a groupBy bridge.");
        }
        int inputStoreIndex = buildHelper.reserveTupleStoreIndex(parent.getTupleSource());
        TupleLifecycle<TriTuple<NewA, NewB, NewC>> tupleLifecycle =
                buildHelper.getAggregatedTupleLifecycle(groupStream.getChildStreamList());
        int outputStoreSize = buildHelper.extractTupleStoreSize(groupStream);
        AbstractGroupNode<QuadTuple<A, B, C, D>, TriTuple<NewA, NewB, NewC>, ?, ?, ?> node =
                nodeConstructor.apply(inputStoreIndex, tupleLifecycle, outputStoreSize);
        buildHelper.addNode(node, this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parent.getTupleSource();
    }

}
