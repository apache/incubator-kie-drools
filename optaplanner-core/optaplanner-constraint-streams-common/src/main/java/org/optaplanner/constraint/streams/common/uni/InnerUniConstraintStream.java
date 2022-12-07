package org.optaplanner.constraint.streams.common.uni;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.common.bi.BiJoinerComber;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.DefaultConstraintJustification;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintBuilder;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

public interface InnerUniConstraintStream<A> extends UniConstraintStream<A> {

    static <A> BiFunction<A, Score<?>, DefaultConstraintJustification> createDefaultJustificationMapping() {
        return (a, score) -> DefaultConstraintJustification.of(score, a);
    }

    static <A> Function<A, Collection<?>> createDefaultIndictedObjectsMapping() {
        return List::of;
    }

    RetrievalSemantics getRetrievalSemantics();

    /**
     * This method returns true if the constraint stream is guaranteed to only produce distinct tuples.
     * See {@link #distinct()} for details.
     *
     * @return true if the guarantee of distinct tuples is provided
     */
    boolean guaranteesDistinct();

    @Override
    default <B> BiConstraintStream<A, B> join(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return join(getConstraintFactory().forEach(otherClass), joiners);
        } else {
            return join(getConstraintFactory().from(otherClass), joiners);
        }
    }

    /**
     * Allows {@link ConstraintFactory#forEachUniquePair(Class)} to reuse the joiner combing logic.
     *
     * @param otherStream never null
     * @param joinerComber never null
     * @param <B>
     * @return never null
     */
    <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream, BiJoinerComber<A, B> joinerComber);

    @Override
    default UniConstraintStream<A> distinct() {
        if (guaranteesDistinct()) {
            return this;
        } else {
            return groupBy(Function.identity());
        }
    }

    @Override
    default <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> penalize(Score_ constraintWeight,
            ToIntFunction<A> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> penalizeLong(Score_ constraintWeight,
            ToLongFunction<A> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> penalizeBigDecimal(Score_ constraintWeight,
            Function<A, BigDecimal> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default UniConstraintBuilder<A, ?> penalizeConfigurable(ToIntFunction<A> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default UniConstraintBuilder<A, ?> penalizeConfigurableLong(ToLongFunction<A> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default UniConstraintBuilder<A, ?> penalizeConfigurableBigDecimal(Function<A, BigDecimal> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> reward(Score_ constraintWeight,
            ToIntFunction<A> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> rewardLong(Score_ constraintWeight,
            ToLongFunction<A> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> rewardBigDecimal(Score_ constraintWeight,
            Function<A, BigDecimal> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default UniConstraintBuilder<A, ?> rewardConfigurable(ToIntFunction<A> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default UniConstraintBuilder<A, ?> rewardConfigurableLong(ToLongFunction<A> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default UniConstraintBuilder<A, ?> rewardConfigurableBigDecimal(Function<A, BigDecimal> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> impact(Score_ constraintWeight,
            ToIntFunction<A> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> impactLong(Score_ constraintWeight,
            ToLongFunction<A> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> impactBigDecimal(Score_ constraintWeight,
            Function<A, BigDecimal> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default UniConstraintBuilder<A, ?> impactConfigurable(ToIntFunction<A> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default UniConstraintBuilder<A, ?> impactConfigurableLong(ToLongFunction<A> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default UniConstraintBuilder<A, ?> impactConfigurableBigDecimal(Function<A, BigDecimal> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.MIXED);
    }

    <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> innerImpact(Score_ constraintWeight,
            ToIntFunction<A> matchWeigher, ScoreImpactType scoreImpactType);

    <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> innerImpact(Score_ constraintWeight,
            ToLongFunction<A> matchWeigher, ScoreImpactType scoreImpactType);

    <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> innerImpact(Score_ constraintWeight,
            Function<A, BigDecimal> matchWeigher, ScoreImpactType scoreImpactType);

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
