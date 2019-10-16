/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream.tri;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

/**
 * A {@link ConstraintStream} that matches three facts.
 * @param <A> the type of the first fact in the tuple.
 * @param <B> the type of the second fact in the tuple.
 * @param <C> the type of the third fact in the tuple.
 * @see ConstraintStream
 */
public interface TriConstraintStream<A, B, C> extends ConstraintStream {

    // ************************************************************************
    // Filter
    // ************************************************************************

    /**
     * Exhaustively test each tuple of facts against the {@link TriPredicate}
     * and match if {@link TriPredicate#test(Object, Object, Object)} returns true.
     * <p>
     * Important: This is slower and less scalable than {@link BiConstraintStream#join(UniConstraintStream, TriJoiner[])}
     * with a proper {@link TriJoiner} predicate (such as {@link Joiners#equal(BiFunction, Function)},
     * because the latter applies hashing and/or indexing, so it doesn't create every combination just to filter it out.
     * @param predicate never null
     * @return never null
     */
    TriConstraintStream<A, B, C> filter(TriPredicate<A, B, C> predicate);

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeLong(String, Score, ToLongTriFunction)} or {@link #penalizeBigDecimal(String, Score, TriFunction)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalize(String constraintName, Score<?> constraintWeight, ToIntTriFunction<A, B, C> matchWeigher) {
        return penalize(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #penalize(String, Score, ToIntTriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight, ToIntTriFunction<A, B, C> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeLong(String constraintName, Score<?> constraintWeight, ToLongTriFunction<A, B, C> matchWeigher) {
        return penalizeLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeLong(String, Score, ToLongTriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeLong(String constraintPackage, String constraintName, Score<?> constraintWeight, ToLongTriFunction<A, B, C> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeBigDecimal(String constraintName, Score<?> constraintWeight, TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return penalizeBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeBigDecimal(String, Score, TriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight, TriFunction<A, B, C, BigDecimal> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeConfigurableLong(String, ToLongTriFunction)} or {@link #penalizeConfigurableBigDecimal(String, TriFunction)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurable(String constraintName, ToIntTriFunction<A, B, C> matchWeigher) {
        return penalizeConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurable(String, ToIntTriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurable(String constraintPackage, String constraintName, ToIntTriFunction<A, B, C> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurableLong(String constraintName, ToLongTriFunction<A, B, C> matchWeigher) {
        return penalizeConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurableLong(String, ToLongTriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurableLong(String constraintPackage, String constraintName, ToLongTriFunction<A, B, C> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurableBigDecimal(String constraintName, TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return penalizeConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurableBigDecimal(String, TriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurableBigDecimal(String constraintPackage, String constraintName, TriFunction<A, B, C, BigDecimal> matchWeigher);


    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardLong(String, Score, ToLongTriFunction)} or {@link #rewardBigDecimal(String, Score, TriFunction)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint reward(String constraintName, Score<?> constraintWeight, ToIntTriFunction<A, B, C> matchWeigher) {
        return reward(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #reward(String, Score, ToIntTriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight, ToIntTriFunction<A, B, C> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardLong(String constraintName, Score<?> constraintWeight, ToLongTriFunction<A, B, C> matchWeigher) {
        return rewardLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #rewardLong(String, Score, ToLongTriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardLong(String constraintPackage, String constraintName, Score<?> constraintWeight, ToLongTriFunction<A, B, C> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardBigDecimal(String constraintName, Score<?> constraintWeight, TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return rewardBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #rewardBigDecimal(String, Score, TriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight, TriFunction<A, B, C, BigDecimal> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardConfigurableLong(String, ToLongTriFunction)} or {@link #rewardConfigurableBigDecimal(String, TriFunction)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurable(String constraintName, ToIntTriFunction<A, B, C> matchWeigher) {
        return rewardConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurable(String, ToIntTriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurable(String constraintPackage, String constraintName, ToIntTriFunction<A, B, C> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurableLong(String constraintName, ToLongTriFunction<A, B, C> matchWeigher) {
        return rewardConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurableLong(String, ToLongTriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurableLong(String constraintPackage, String constraintName, ToLongTriFunction<A, B, C> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurableBigDecimal(String constraintName, TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return rewardConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurableBigDecimal(String, TriFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurableBigDecimal(String constraintPackage, String constraintName, TriFunction<A, B, C, BigDecimal> matchWeigher);

}
