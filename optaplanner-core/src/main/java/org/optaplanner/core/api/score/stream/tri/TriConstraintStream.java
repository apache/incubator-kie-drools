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

import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.common.Joiners;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

/**
 * A {@link ConstraintStream} that matches three facts.
 * @param <A> the type of the first matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @param <B> the type of the second matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @param <C> the type of the third matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @see ConstraintStream
 */
public interface TriConstraintStream<A, B, C> extends ConstraintStream {

    /**
     * Exhaustively test each tuple of facts against the {@link TriPredicate}
     * and match if {@link TriPredicate#test(Object, Object, Object)} returns true.
     * <p>
     * Important: This is slower and less scalable than {@link BiConstraintStream#join(UniConstraintStream, TriJoiner[])}
     * with a proper {@link TriJoiner} predicate (such as {@link Joiners#equalTo(BiFunction, Function)},
     * because the latter applies hashing and/or indexing, so it doesn't create every combination just to filter it out.
     * @param predicate never null
     * @return never null
     */
    TriConstraintStream<A, B, C> filter(TriPredicate<A, B, C> predicate);

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} for each match.
     */
    void penalize();

    // TODO introduce TriFunctions
//    /**
//     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
//     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
//     */
//    void penalizeInt(ToIntBiFunction<A, B> matchWeigher);
//
//    /**
//     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
//     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
//     */
//    void penalizeLong(ToLongBiFunction<A, B> matchWeigher);
//
//    /**
//     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
//     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
//     */
//    void penalizeBigDecimal(BiFunction<A, B, BigDecimal> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} for each match.
     */
    void reward();

    // TODO introduce TriFunctions
//    /**
//     * Positively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
//     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
//     */
//    void rewardInt(ToIntBiFunction<A, B> matchWeigher);
//
//    /**
//     * Positively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
//     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
//     */
//    void rewardLong(ToLongBiFunction<A, B> matchWeigher);
//
//    /**
//     * Positively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
//     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
//     */
//    void rewardBigDecimal(BiFunction<A, B, BigDecimal> matchWeigher);

}
