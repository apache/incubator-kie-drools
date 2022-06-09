package org.optaplanner.constraint.streams.common.quad;

import static org.optaplanner.constraint.streams.common.ScoreImpactType.MIXED;
import static org.optaplanner.constraint.streams.common.ScoreImpactType.PENALTY;
import static org.optaplanner.constraint.streams.common.ScoreImpactType.REWARD;

import java.math.BigDecimal;

import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;

public interface InnerQuadConstraintStream<A, B, C, D> extends QuadConstraintStream<A, B, C, D> {

    /**
     * This method will return true if the constraint stream is guaranteed to only produce distinct tuples.
     * See {@link #distinct()} for details.
     *
     * @return true if the guarantee of distinct tuples is provided
     */
    boolean guaranteesDistinct();

    @Override
    default QuadConstraintStream<A, B, C, D> distinct() {
        if (guaranteesDistinct()) {
            return this;
        } else {
            return groupBy((a, b, c, d) -> a, (a, b, c, d) -> b, (a, b, c, d) -> c, (a, b, c, d) -> d);
        }
    }

    @Override
    default Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return impactScore(constraintPackage, constraintName, constraintWeight, matchWeigher, PENALTY);
    }

    @Override
    default Constraint penalizeLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return impactScoreLong(constraintPackage, constraintName, constraintWeight, matchWeigher, PENALTY);
    }

    @Override
    default Constraint penalizeBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return impactScoreBigDecimal(constraintPackage, constraintName, constraintWeight, matchWeigher, PENALTY);
    }

    @Override
    default Constraint penalizeConfigurable(String constraintPackage, String constraintName,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return impactScoreConfigurable(constraintPackage, constraintName, matchWeigher, PENALTY);
    }

    @Override
    default Constraint penalizeConfigurableLong(String constraintPackage, String constraintName,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return impactScoreConfigurableLong(constraintPackage, constraintName, matchWeigher, PENALTY);
    }

    @Override
    default Constraint penalizeConfigurableBigDecimal(String constraintPackage, String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return impactScoreConfigurableBigDecimal(constraintPackage, constraintName, matchWeigher, PENALTY);
    }

    @Override
    default Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return impactScore(constraintPackage, constraintName, constraintWeight, matchWeigher, REWARD);
    }

    @Override
    default Constraint rewardLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return impactScoreLong(constraintPackage, constraintName, constraintWeight, matchWeigher, REWARD);
    }

    @Override
    default Constraint rewardBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return impactScoreBigDecimal(constraintPackage, constraintName, constraintWeight, matchWeigher, REWARD);
    }

    @Override
    default Constraint rewardConfigurable(String constraintPackage, String constraintName,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return impactScoreConfigurable(constraintPackage, constraintName, matchWeigher, REWARD);
    }

    @Override
    default Constraint rewardConfigurableLong(String constraintPackage, String constraintName,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return impactScoreConfigurableLong(constraintPackage, constraintName, matchWeigher, REWARD);
    }

    @Override
    default Constraint rewardConfigurableBigDecimal(String constraintPackage, String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return impactScoreConfigurableBigDecimal(constraintPackage, constraintName, matchWeigher, REWARD);
    }

    @Override
    default Constraint impact(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return impactScore(constraintPackage, constraintName, constraintWeight, matchWeigher, MIXED);
    }

    @Override
    default Constraint impactLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return impactScoreLong(constraintPackage, constraintName, constraintWeight, matchWeigher, MIXED);
    }

    @Override
    default Constraint impactBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return impactScoreBigDecimal(constraintPackage, constraintName, constraintWeight, matchWeigher, MIXED);
    }

    @Override
    default Constraint impactConfigurable(String constraintPackage, String constraintName,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return impactScoreConfigurable(constraintPackage, constraintName, matchWeigher, MIXED);
    }

    @Override
    default Constraint impactConfigurableLong(String constraintPackage, String constraintName,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return impactScoreConfigurableLong(constraintPackage, constraintName, matchWeigher, MIXED);
    }

    @Override
    default Constraint impactConfigurableBigDecimal(String constraintPackage, String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return impactScoreConfigurableBigDecimal(constraintPackage, constraintName, matchWeigher, MIXED);
    }

    Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType);

    Constraint impactScoreLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType);

    Constraint impactScoreBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher, ScoreImpactType impactType);

    Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType);

    Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType);

    Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher, ScoreImpactType impactType);

}
