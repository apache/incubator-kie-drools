package org.optaplanner.constraint.streams.common.tri;

import java.math.BigDecimal;

import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintBuilder;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;

public interface InnerTriConstraintStream<A, B, C> extends TriConstraintStream<A, B, C> {

    RetrievalSemantics getRetrievalSemantics();

    /**
     * This method will return true if the constraint stream is guaranteed to only produce distinct tuples.
     * See {@link #distinct()} for details.
     *
     * @return true if the guarantee of distinct tuples is provided
     */
    boolean guaranteesDistinct();

    @Override
    default <D> QuadConstraintStream<A, B, C, D> join(Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return join(getConstraintFactory().forEach(otherClass), joiners);
        } else {
            return join(getConstraintFactory().from(otherClass), joiners);
        }
    }

    @Override
    default TriConstraintStream<A, B, C> distinct() {
        if (guaranteesDistinct()) {
            return this;
        } else {
            return groupBy((a, b, c) -> a, (a, b, c) -> b, (a, b, c) -> c);
        }
    }

    @Override
    default TriConstraintBuilder<A, B, C> penalize(Score<?> constraintWeight, ToIntTriFunction<A, B, C> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default TriConstraintBuilder<A, B, C> penalizeLong(Score<?> constraintWeight, ToLongTriFunction<A, B, C> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default TriConstraintBuilder<A, B, C> penalizeBigDecimal(Score<?> constraintWeight,
            TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default TriConstraintBuilder<A, B, C> penalizeConfigurable(ToIntTriFunction<A, B, C> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default TriConstraintBuilder<A, B, C> penalizeConfigurableLong(ToLongTriFunction<A, B, C> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default TriConstraintBuilder<A, B, C> penalizeConfigurableBigDecimal(TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.PENALTY);
    }

    @Override
    default TriConstraintBuilder<A, B, C> reward(Score<?> constraintWeight, ToIntTriFunction<A, B, C> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default TriConstraintBuilder<A, B, C> rewardLong(Score<?> constraintWeight, ToLongTriFunction<A, B, C> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default TriConstraintBuilder<A, B, C> rewardBigDecimal(Score<?> constraintWeight,
            TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default TriConstraintBuilder<A, B, C> rewardConfigurable(ToIntTriFunction<A, B, C> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default TriConstraintBuilder<A, B, C> rewardConfigurableLong(ToLongTriFunction<A, B, C> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default TriConstraintBuilder<A, B, C> rewardConfigurableBigDecimal(TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.REWARD);
    }

    @Override
    default TriConstraintBuilder<A, B, C> impact(Score<?> constraintWeight, ToIntTriFunction<A, B, C> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default TriConstraintBuilder<A, B, C> impactLong(Score<?> constraintWeight, ToLongTriFunction<A, B, C> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default TriConstraintBuilder<A, B, C> impactBigDecimal(Score<?> constraintWeight,
            TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return innerImpact(constraintWeight, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default TriConstraintBuilder<A, B, C> impactConfigurable(ToIntTriFunction<A, B, C> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default TriConstraintBuilder<A, B, C> impactConfigurableLong(ToLongTriFunction<A, B, C> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.MIXED);
    }

    @Override
    default TriConstraintBuilder<A, B, C> impactConfigurableBigDecimal(TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return innerImpact(null, matchWeigher, ScoreImpactType.MIXED);
    }

    TriConstraintBuilder<A, B, C> innerImpact(Score<?> constraintWeight, ToIntTriFunction<A, B, C> matchWeigher,
            ScoreImpactType scoreImpactType);

    TriConstraintBuilder<A, B, C> innerImpact(Score<?> constraintWeight, ToLongTriFunction<A, B, C> matchWeigher,
            ScoreImpactType scoreImpactType);

    TriConstraintBuilder<A, B, C> innerImpact(Score<?> constraintWeight, TriFunction<A, B, C, BigDecimal> matchWeigher,
            ScoreImpactType scoreImpactType);

    @Override
    default Constraint penalize(String constraintName, Score<?> constraintWeight) {
        return penalize(constraintWeight)
                .asConstraint(constraintName);
    }

    @Override
    default Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        return penalize(constraintWeight)
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
        return reward(constraintWeight)
                .asConstraint(constraintName);
    }

    @Override
    default Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        return reward(constraintWeight)
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
        return impact(constraintWeight)
                .asConstraint(constraintName);
    }

    @Override
    default Constraint impact(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        return impact(constraintWeight)
                .asConstraint(constraintPackage, constraintName);
    }

}
