package org.optaplanner.constraint.streams.bavet.tri;

import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetJoinConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetJoinBridgeTriConstraintStream<Solution_, A, B, C>
        extends BavetAbstractTriConstraintStream<Solution_, A, B, C> {

    private final BavetAbstractTriConstraintStream<Solution_, A, B, C> parent;
    private final boolean isLeftBridge;

    private BavetJoinConstraintStream<Solution_> joinStream;

    public BavetJoinBridgeTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent, boolean isLeftBridge) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        this.isLeftBridge = isLeftBridge;
    }

    @Override
    public boolean guaranteesDistinct() {
        return parent.guaranteesDistinct();
    }

    public void setJoinStream(BavetJoinConstraintStream<Solution_> joinStream) {
        this.joinStream = joinStream;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parent.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    public BavetAbstractTriConstraintStream<Solution_, A, B, C> getParent() {
        return parent;
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parent.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        // Do nothing. BavetJoinQuadConstraintStream, etc build everything.
    }

    @Override
    public String toString() {
        return "JoinBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
