package org.optaplanner.constraint.streams.common.bi;

import static org.optaplanner.constraint.streams.common.RetrievalSemantics.STANDARD;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.DefaultConstraintJustification;
import org.optaplanner.core.api.score.stream.bi.BiConstraintBuilder;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;

public interface InnerBiConstraintStream<A, B> extends BiConstraintStream<A, B> {

    static <A, B> TriFunction<A, B, Score<?>, DefaultConstraintJustification> getDefaultJustificationMapping() {
        return (a, b, score) -> DefaultConstraintJustification.of(score, a, b);
    }

    static <A, B> BiFunction<A, B, Collection<?>> getDefaultIndictedObjectsMapping() {
        return List::of;
    }

    RetrievalSemantics getRetrievalSemantics();

    /**
     * This method will return true if the constraint stream is guaranteed to only produce distinct tuples.
     * See {@link #distinct()} for details.
     *
     * @return true if the guarantee of distinct tuples is provided
     */
    boolean guaranteesDistinct();

    @Override
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        if (getRetrievalSemantics() == STANDARD) {
            return join(getConstraintFactory().forEach(otherClass), joiners);
        } else {
            return join(getConstraintFactory().from(otherClass), joiners);
        }
    }

    @Override
    default BiConstraintStream<A, B> distinct() {
        if (guaranteesDistinct()) {
            return this;
        } else {
            return groupBy((a, b) -> a, (a, b) -> b);
        }
    }

    @Override
    default <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> penalize(Score_ constraintWeight,
            ToIntBiFunction<A, B> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> penalizeLong(Score_ constraintWeight,
            ToLongBiFunction<A, B> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> penalizeBigDecimal(Score_ constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default BiConstraintBuilder<A, B, ?> penalizeConfigurable(ToIntBiFunction<A, B> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default BiConstraintBuilder<A, B, ?> penalizeConfigurableLong(ToLongBiFunction<A, B> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default BiConstraintBuilder<A, B, ?> penalizeConfigurableBigDecimal(BiFunction<A, B, BigDecimal> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> reward(Score_ constraintWeight,
            ToIntBiFunction<A, B> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> rewardLong(Score_ constraintWeight,
            ToLongBiFunction<A, B> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> rewardBigDecimal(Score_ constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default BiConstraintBuilder<A, B, ?> rewardConfigurable(ToIntBiFunction<A, B> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default BiConstraintBuilder<A, B, ?> rewardConfigurableLong(ToLongBiFunction<A, B> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default BiConstraintBuilder<A, B, ?> rewardConfigurableBigDecimal(BiFunction<A, B, BigDecimal> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> impact(Score_ constraintWeight,
            ToIntBiFunction<A, B> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> impactLong(Score_ constraintWeight,
            ToLongBiFunction<A, B> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> impactBigDecimal(Score_ constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default BiConstraintBuilder<A, B, ?> impactConfigurable(ToIntBiFunction<A, B> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default BiConstraintBuilder<A, B, ?> impactConfigurableLong(ToLongBiFunction<A, B> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default BiConstraintBuilder<A, B, ?> impactConfigurableBigDecimal(BiFunction<A, B, BigDecimal> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.MIXED);
    }

    <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> innerImpact(Score_ constraintWeight,
            ToIntBiFunction<A, B> matchWeigher,
            ScoreImpactType scoreImpactType);

    <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> innerImpact(Score_ constraintWeight,
            ToLongBiFunction<A, B> matchWeigher,
            ScoreImpactType scoreImpactType);

    <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> innerImpact(Score_ constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher,
            ScoreImpactType scoreImpactType);

    @Override
    default Constraint penalize(String constraintName, Score<?> constraintWeight) {
        return penalize((Score) constraintWeight)
                .asConstraint(constraintName);
    }

    @Override
    default Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        return penalize((Score) constraintWeight)
                .asConstraint(constraintPackage, constraintName);
    }

    @Override
    default Constraint penalizeConfigurable(String constraintName) {
        return penalizeConfigurable()
                .asConstraint(constraintName);
    }

    @Override
    default Constraint penalizeConfigurable(String constraintPackage, String constraintName) {
        return penalizeConfigurable()
                .asConstraint(constraintPackage, constraintName);
    }

    @Override
    default Constraint reward(String constraintName, Score<?> constraintWeight) {
        return reward((Score) constraintWeight)
                .asConstraint(constraintName);
    }

    @Override
    default Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        return reward((Score) constraintWeight)
                .asConstraint(constraintPackage, constraintName);
    }

    @Override
    default Constraint rewardConfigurable(String constraintName) {
        return rewardConfigurable()
                .asConstraint(constraintName);
    }

    @Override
    default Constraint rewardConfigurable(String constraintPackage, String constraintName) {
        return penalizeConfigurable()
                .asConstraint(constraintPackage, constraintName);
    }

    @Override
    default Constraint impact(String constraintName, Score<?> constraintWeight) {
        return impact((Score) constraintWeight)
                .asConstraint(constraintName);
    }

    @Override
    default Constraint impact(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        return impact((Score) constraintWeight)
                .asConstraint(constraintPackage, constraintName);
    }

}
