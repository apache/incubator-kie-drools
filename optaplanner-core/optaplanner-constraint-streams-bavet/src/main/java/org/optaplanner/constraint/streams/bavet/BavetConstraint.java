package org.optaplanner.constraint.streams.bavet;

import java.util.Set;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetScoringConstraintStream;
import org.optaplanner.constraint.streams.common.AbstractConstraint;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.core.api.score.Score;

public final class BavetConstraint<Solution_>
        extends AbstractConstraint<Solution_, BavetConstraint<Solution_>, BavetConstraintFactory<Solution_>> {

    private final BavetScoringConstraintStream<Solution_> scoringConstraintStream;

    public BavetConstraint(BavetConstraintFactory<Solution_> constraintFactory, String constraintPackage,
            String constraintName, Function<Solution_, Score<?>> constraintWeightExtractor,
            ScoreImpactType scoreImpactType, boolean isConstraintWeightConfigurable,
            BavetScoringConstraintStream<Solution_> scoringConstraintStream) {
        super(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor, scoreImpactType,
                isConstraintWeightConfigurable);
        this.scoringConstraintStream = scoringConstraintStream;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public String toString() {
        return "BavetConstraint(" + getConstraintId() + ")";
    }

    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        scoringConstraintStream.collectActiveConstraintStreams(constraintStreamSet);
    }

}
