package org.optaplanner.constraint.streams.bavet.bi;

import java.util.List;
import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.GroupNodeConstructor;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.tri.BavetGroupTriConstraintStream;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

final class BavetTriGroupBridgeBiConstraintStream<Solution_, A, B, NewA, NewB, NewC>
        extends BavetAbstractBiConstraintStream<Solution_, A, B> {

    private final BavetAbstractBiConstraintStream<Solution_, A, B> parent;
    private BavetGroupTriConstraintStream<Solution_, NewA, NewB, NewC> groupStream;
    private final GroupNodeConstructor<TriTuple<NewA, NewB, NewC>> nodeConstructor;

    public BavetTriGroupBridgeBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            GroupNodeConstructor<TriTuple<NewA, NewB, NewC>> nodeConstructor) {
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
        List<? extends ConstraintStream> groupStreamChildList = groupStream.getChildStreamList();
        nodeConstructor.build(buildHelper, parent.getTupleSource(), groupStream, groupStreamChildList, this, childStreamList,
                constraintFactory.getEnvironmentMode());
    }

    @Override
    public BavetAbstractConstraintStream<Solution_> getTupleSource() {
        return parent.getTupleSource();
    }
}
