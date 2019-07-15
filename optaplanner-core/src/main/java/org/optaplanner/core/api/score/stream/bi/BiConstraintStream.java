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

package org.optaplanner.core.api.score.stream.bi;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.common.Joiners;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;

/**
 * A {@link ConstraintStream} that matches two facts.
 * @param <A> the type of the first matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @param <B> the type of the second matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @see ConstraintStream
 */
public interface BiConstraintStream<A, B> extends ConstraintStream {

    // ************************************************************************
    // Filter
    // ************************************************************************

    /**
     * Exhaustively test each tuple of facts against the {@link BiPredicate}
     * and match if {@link BiPredicate#test(Object, Object)} returns true.
     * <p>
     * Important: This is slower and less scalable than {@link UniConstraintStream#join(UniConstraintStream, BiJoiner[])}
     * with a proper {@link BiJoiner} predicate (such as {@link Joiners#equalTo(Function, Function)},
     * because the latter applies hashing and/or indexing, so it doesn't create every combination just to filter it out.
     * @param predicate never null
     * @return never null
     */
    BiConstraintStream<A, B> filter(BiPredicate<A, B> predicate);

    // ************************************************************************
    // Join
    // ************************************************************************

    /**
     * Create a new {@link TriConstraintStream} for every combination of [A, B] and C.
     * <p>
     * Important: {@link TriConstraintStream#filter(TriPredicate)}  Filtering} this is slower and less scalable
     * than a {@link #join(UniConstraintStream, TriJoiner)},
     * because it doesn't apply hashing and/or indexing on the properties,
     * so it creates and checks every combination of [A, B] and C.
     * @param otherStream never null
     * @param <C> the type of the third matched fact
     * @return a stream that matches every combination of [A, B] and C
     */
    <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream);

    /**
     * Create a new {@link TriConstraintStream} for every combination of [A, B] and C for which the {@link BiJoiner}
     * is true (for the properties it extracts from both facts).
     * <p>
     * Important: This is faster and more scalable than a {@link #join(UniConstraintStream) join}
     * followed by a {@link TriConstraintStream#filter(TriPredicate) filter},
     * because it applies hashing and/or indexing on the properties,
     * so it doesn't create nor checks every combination of [A, B] and C.
     * @param otherStream never null
     * @param joiner never null
     * @param <C> the type of the third matched fact
     * @return a stream that matches every combination of [A, B] and C for which the {@link BiJoiner} is true
     */
    <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream, TriJoiner<A, B, C> joiner);

    /**
     * Create a new {@link TriConstraintStream} for every combination of [A, B] and C.
     * <p>
     * Important: {@link TriConstraintStream#filter(TriPredicate)}  Filtering} this is slower and less scalable
     * than a {@link #join(Class, TriJoiner)},
     * because it doesn't apply hashing and/or indexing on the properties,
     * so it creates and checks every combination of [A, B] and C.
     * <p>
     * This method is syntactic sugar for {@link #join(UniConstraintStream)}.
     * @param otherClass never null
     * @param <C> the type of the third matched fact
     * @return a stream that matches every combination of [A, B] and C
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass) {
        return join(getConstraint().from(otherClass));
    }

    /**
     * Create a new {@link TriConstraintStream} for every combination of [A, B] and C for which the {@link BiJoiner}
     * is true (for the properties it extracts from both facts).
     * <p>
     * Important: This is faster and more scalable than a {@link #join(Class, TriJoiner) join}
     * followed by a {@link TriConstraintStream#filter(TriPredicate) filter},
     * because it applies hashing and/or indexing on the properties,
     * so it doesn't create nor checks every combination of [A, B] and C.
     * <p>
     * This method is syntactic sugar for {@link #join(UniConstraintStream, TriJoiner)}.
     * <p>
     * This method has overloaded methods with multiple {@link TriJoiner} parameters.
     * @param otherClass never null
     * @param joiner never null
     * @param <C> the type of the third matched fact
     * @return a stream that matches every combination of [A, B] and C for which the {@link BiJoiner} is true
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C> joiner) {
        return join(getConstraint().from(otherClass), joiner);
    }

    /**
     * @see #join(Class, TriJoiner)
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2) {
        return join(otherClass, AbstractTriJoiner.merge(joiner1, joiner2));
    }

    /**
     * @see #join(Class, TriJoiner)
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2, TriJoiner<A, B, C> joiner3) {
        return join(otherClass, AbstractTriJoiner.merge(joiner1, joiner2, joiner3));
    }

    /**
     * @see #join(Class, TriJoiner)
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2, TriJoiner<A, B, C> joiner3, TriJoiner<A, B, C> joiner4) {
        return join(otherClass, AbstractTriJoiner.merge(joiner1, joiner2, joiner3, joiner4));
    }

    /**
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link BiJoiner} parameters.
     * @see #join(Class, TriJoiner)
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        return join(otherClass, AbstractTriJoiner.merge(joiners));
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    <GroupKey_> UniConstraintStream<GroupKey_> groupBy(
            BiFunction<A, B, GroupKey_> groupKeyMapping);

    <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            BiFunction<A, B, GroupKey_> groupKeyMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector);

    <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping,
            BiFunction<A, B, GroupKeyB_> groupKeyBMapping);

    <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping,
            BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector);

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} for each match.
     */
    void penalize();

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeLong(ToLongBiFunction)} or {@link #penalizeBigDecimal(BiFunction)}.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void penalize(ToIntBiFunction<A, B> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void penalizeLong(ToLongBiFunction<A, B> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void penalizeBigDecimal(BiFunction<A, B, BigDecimal> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} for each match.
     */
    void reward();

    /**
     * Positively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * <p>
     * For non-int {@link Score} types use {@link #rewardLong(ToLongBiFunction)} or {@link #rewardBigDecimal(BiFunction)}.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void reward(ToIntBiFunction<A, B> matchWeigher);

    /**
     * Positively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void rewardLong(ToLongBiFunction<A, B> matchWeigher);

    /**
     * Positively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void rewardBigDecimal(BiFunction<A, B, BigDecimal> matchWeigher);

}
