package org.optaplanner.constraint.streams.bavet.common;

import java.util.Set;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.BavetConstraint;
import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.constraint.streams.common.AbstractConstraintStream;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

public abstract class BavetAbstractConstraintStream<Solution_> extends AbstractConstraintStream<Solution_> {

    protected final BavetConstraintFactory<Solution_> constraintFactory;

    public BavetAbstractConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            RetrievalSemantics retrievalSemantics) {
        super(retrievalSemantics);
        this.constraintFactory = constraintFactory;
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    protected BavetConstraint<Solution_> buildConstraint(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ScoreImpactType impactType,
            BavetScoringConstraintStream<Solution_> scoringConstraintStream) {
        Function<Solution_, Score<?>> constraintWeightExtractor = buildConstraintWeightExtractor(
                constraintPackage, constraintName, constraintWeight);
        BavetConstraint<Solution_> constraint =
                new BavetConstraint<>(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor,
                        impactType, false, scoringConstraintStream);
        scoringConstraintStream.setConstraint(constraint);
        return constraint;
    }

    protected BavetConstraint<Solution_> buildConstraintConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType,
            BavetScoringConstraintStream<Solution_> scoringConstraintStream) {
        Function<Solution_, Score<?>> constraintWeightExtractor = buildConstraintWeightExtractor(
                constraintPackage, constraintName);
        BavetConstraint<Solution_> constraint =
                new BavetConstraint<>(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor,
                        impactType, true, scoringConstraintStream);
        scoringConstraintStream.setConstraint(constraint);
        return constraint;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    public abstract void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet);

    public abstract ConstraintStream getTupleSource();

    public abstract <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper);

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected <A> BavetAbstractUniConstraintStream<Solution_, A> assertBavetUniConstraintStream(
            UniConstraintStream<A> otherStream) {
        if (!(otherStream instanceof BavetAbstractUniConstraintStream)) {
            throw new IllegalStateException("The streams (" + this + ", " + otherStream
                    + ") are not built from the same " + ConstraintFactory.class.getSimpleName() + ".");
        }
        BavetAbstractUniConstraintStream<Solution_, A> other = (BavetAbstractUniConstraintStream<Solution_, A>) otherStream;
        if (constraintFactory != other.getConstraintFactory()) {
            throw new IllegalStateException("The streams (" + this + ", " + other
                    + ") are built from different constraintFactories (" + constraintFactory + ", "
                    + other.getConstraintFactory()
                    + ").");
        }
        return other;
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetConstraintFactory<Solution_> getConstraintFactory() {
        return constraintFactory;
    }

}
