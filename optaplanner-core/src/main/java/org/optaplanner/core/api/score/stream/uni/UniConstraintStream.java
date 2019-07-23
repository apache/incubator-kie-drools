/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream.uni;

import java.math.BigDecimal;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;

/**
 * A {@link ConstraintStream} that matches one fact.
 * @param <A> the type of the matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @see ConstraintStream
 */
public interface UniConstraintStream<A> extends ConstraintStream {

    // ************************************************************************
    // Filter
    // ************************************************************************

    /**
     * Exhaustively test each fact against the {@link Predicate}
     * and match if {@link Predicate#test(Object)} returns true.
     * @param predicate never null
     * @return never null
     */
    UniConstraintStream<A> filter(Predicate<A> predicate);

    // ************************************************************************
    // Join
    // ************************************************************************

    /**
     * Create a new {@link BiConstraintStream} for every combination of A and B.
     * <p>
     * Important: {@link BiConstraintStream#filter(BiPredicate) Filtering} this is slower and less scalable
     * than a {@link #join(UniConstraintStream, BiJoiner)},
     * because it doesn't apply hashing and/or indexing on the properties,
     * so it creates and checks every combination of A and B.
     * @param otherStream never null
     * @param <B> the type of the second matched fact
     * @return a stream that matches every combination of A and B
     */
    <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream);

    /**
     * Create a new {@link BiConstraintStream} for every combination of A and B for which the {@link BiJoiner}
     * is true (for the properties it extracts from both facts).
     * <p>
     * Important: This is faster and more scalable than a {@link #join(UniConstraintStream) join}
     * followed by a {@link BiConstraintStream#filter(BiPredicate) filter},
     * because it applies hashing and/or indexing on the properties,
     * so it doesn't create nor checks every combination of A and B.
     * @param otherStream never null
     * @param joiner never null
     * @param <B> the type of the second matched fact
     * @return a stream that matches every combination of A and B for which the {@link BiJoiner} is true
     */
    <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream, BiJoiner<A, B> joiner);

    /**
     * Create a new {@link BiConstraintStream} for every combination of A and B.
     * <p>
     * Important: {@link BiConstraintStream#filter(BiPredicate) Filtering} this is slower and less scalable
     * than a {@link #join(Class, BiJoiner)},
     * because it doesn't apply hashing and/or indexing on the properties,
     * so it creates and checks every combination of A and B.
     * <p>
     * This method is syntactic sugar for {@link #join(UniConstraintStream)}.
     * @param otherClass never null
     * @param <B> the type of the second matched fact
     * @return a stream that matches every combination of A and B
     */
    default <B> BiConstraintStream<A, B> join(Class<B> otherClass) {
        return join(getConstraint().from(otherClass));
    }

    /**
     * Create a new {@link BiConstraintStream} for every combination of A and B
     * for which the {@link BiJoiner} is true (for the properties it extracts from both facts).
     * <p>
     * Important: This is faster and more scalable than a {@link #join(Class) join}
     * followed by a {@link BiConstraintStream#filter(BiPredicate) filter},
     * because it applies hashing and/or indexing on the properties,
     * so it doesn't create nor checks every combination of A and B.
     * <p>
     * This method is syntactic sugar for {@link #join(UniConstraintStream, BiJoiner)}.
     * <p>
     * This method has overloaded methods with multiple {@link BiJoiner} parameters.
     * @param otherClass never null
     * @param joiner never null
     * @param <B> the type of the second matched fact
     * @return a stream that matches every combination of A and B for which the {@link BiJoiner} is true
     */
    default <B> BiConstraintStream<A, B> join(Class<B> otherClass, BiJoiner<A, B> joiner) {
        return join(getConstraint().from(otherClass), joiner);
    }

    /**
     * @see #join(Class, BiJoiner)
     */
    default <B> BiConstraintStream<A, B> join(Class<B> otherClass, BiJoiner<A, B> joiner1, BiJoiner<A, B> joiner2) {
        return join(otherClass, AbstractBiJoiner.merge(joiner1, joiner2));
    }

    /**
     * @see #join(Class, BiJoiner)
     */
    default <B> BiConstraintStream<A, B> join(Class<B> otherClass, BiJoiner<A, B> joiner1, BiJoiner<A, B> joiner2,
            BiJoiner<A, B> joiner3) {
        return join(otherClass, AbstractBiJoiner.merge(joiner1, joiner2, joiner3));
    }

    /**
     * @see #join(Class, BiJoiner)
     */
    default <B> BiConstraintStream<A, B> join(Class<B> otherClass, BiJoiner<A, B> joiner1, BiJoiner<A, B> joiner2,
            BiJoiner<A, B> joiner3, BiJoiner<A, B> joiner4) {
        return join(otherClass, AbstractBiJoiner.merge(joiner1, joiner2, joiner3, joiner4));
    }

    /**
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link BiJoiner} parameters.
     * @see #join(Class, BiJoiner)
     */
    default <B> BiConstraintStream<A, B> join(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        return join(otherClass, AbstractBiJoiner.merge(joiners));
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    <GroupKey_> UniConstraintStream<GroupKey_> groupBy(
            Function<A, GroupKey_> groupKeyMapping);

    <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            Function<A, GroupKey_> groupKeyMapping,
            UniConstraintCollector<A, ResultContainer_, Result_> collector);

    <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping,
            Function<A, GroupKeyB_> groupKeyBMapping);

    <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping,
            Function<A, GroupKeyB_> groupKeyBMapping,
            UniConstraintCollector<A, ResultContainer_, Result_> collector);

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
     * For non-int {@link Score} types use {@link #penalizeLong(ToLongFunction)} or {@link #penalizeBigDecimal(Function)}.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void penalize(ToIntFunction<A> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void penalizeLong(ToLongFunction<A> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void penalizeBigDecimal(Function<A, BigDecimal> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} for each match.
     */
    void reward();

    /**
     * Positively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * <p>
     * For non-int {@link Score} types use {@link #rewardLong(ToLongFunction)} or {@link #rewardBigDecimal(Function)}.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void reward(ToIntFunction<A> matchWeigher);

    /**
     * Positively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void rewardLong(ToLongFunction<A> matchWeigher);

    /**
     * Positively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     */
    void rewardBigDecimal(Function<A, BigDecimal> matchWeigher);

}
