/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.constraint.streams.bi;

import static org.optaplanner.constraint.streams.common.RetrievalSemantics.STANDARD;
import static org.optaplanner.constraint.streams.common.ScoreImpactType.MIXED;
import static org.optaplanner.constraint.streams.common.ScoreImpactType.PENALTY;
import static org.optaplanner.constraint.streams.common.ScoreImpactType.REWARD;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;

public interface InnerBiConstraintStream<A, B> extends BiConstraintStream<A, B> {

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
    default Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntBiFunction<A, B> matchWeigher) {
        return impactScore(constraintPackage, constraintName, constraintWeight, matchWeigher, PENALTY);
    }

    @Override
    default Constraint penalizeLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongBiFunction<A, B> matchWeigher) {
        return impactScoreLong(constraintPackage, constraintName, constraintWeight, matchWeigher, PENALTY);
    }

    @Override
    default Constraint penalizeBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return impactScoreBigDecimal(constraintPackage, constraintName, constraintWeight, matchWeigher, PENALTY);
    }

    @Override
    default Constraint penalizeConfigurable(String constraintPackage, String constraintName,
            ToIntBiFunction<A, B> matchWeigher) {
        return impactScoreConfigurable(constraintPackage, constraintName, matchWeigher, PENALTY);
    }

    @Override
    default Constraint penalizeConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher) {
        return impactScoreConfigurableLong(constraintPackage, constraintName, matchWeigher, PENALTY);
    }

    @Override
    default Constraint penalizeConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return impactScoreConfigurableBigDecimal(constraintPackage, constraintName, matchWeigher, PENALTY);
    }

    @Override
    default Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntBiFunction<A, B> matchWeigher) {
        return impactScore(constraintPackage, constraintName, constraintWeight, matchWeigher, REWARD);
    }

    @Override
    default Constraint rewardLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongBiFunction<A, B> matchWeigher) {
        return impactScoreLong(constraintPackage, constraintName, constraintWeight, matchWeigher, REWARD);
    }

    @Override
    default Constraint rewardBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return impactScoreBigDecimal(constraintPackage, constraintName, constraintWeight, matchWeigher, REWARD);
    }

    @Override
    default Constraint rewardConfigurable(String constraintPackage, String constraintName,
            ToIntBiFunction<A, B> matchWeigher) {
        return impactScoreConfigurable(constraintPackage, constraintName, matchWeigher, REWARD);
    }

    @Override
    default Constraint rewardConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher) {
        return impactScoreConfigurableLong(constraintPackage, constraintName, matchWeigher, REWARD);
    }

    @Override
    default Constraint rewardConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return impactScoreConfigurableBigDecimal(constraintPackage, constraintName, matchWeigher, REWARD);
    }

    @Override
    default Constraint impact(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntBiFunction<A, B> matchWeigher) {
        return impactScore(constraintPackage, constraintName, constraintWeight, matchWeigher, MIXED);
    }

    @Override
    default Constraint impactLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongBiFunction<A, B> matchWeigher) {
        return impactScoreLong(constraintPackage, constraintName, constraintWeight, matchWeigher, MIXED);
    }

    @Override
    default Constraint impactBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return impactScoreBigDecimal(constraintPackage, constraintName, constraintWeight, matchWeigher, MIXED);
    }

    @Override
    default Constraint impactConfigurable(String constraintPackage, String constraintName,
            ToIntBiFunction<A, B> matchWeigher) {
        return impactScoreConfigurable(constraintPackage, constraintName, matchWeigher, MIXED);
    }

    @Override
    default Constraint impactConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher) {
        return impactScoreConfigurableLong(constraintPackage, constraintName, matchWeigher, MIXED);
    }

    @Override
    default Constraint impactConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return impactScoreConfigurableBigDecimal(constraintPackage, constraintName, matchWeigher, MIXED);
    }

    Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntBiFunction<A, B> matchWeigher, ScoreImpactType impactType);

    Constraint impactScoreLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongBiFunction<A, B> matchWeigher, ScoreImpactType impactType);

    Constraint impactScoreBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher, ScoreImpactType impactType);

    Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntBiFunction<A, B> matchWeigher, ScoreImpactType impactType);

    Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher, ScoreImpactType impactType);

    Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher, ScoreImpactType impactType);

}
