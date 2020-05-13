/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bi.BiConstraintStreamHelper;
import org.optaplanner.core.impl.score.stream.tri.NoneTriJoiner;

/**
 * A {@link ConstraintStream} that matches two facts.
 *
 * @param <A> the type of the first fact in the tuple.
 * @param <B> the type of the second fact in the tuple.
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
     * Important: This is slower and less scalable than {@link UniConstraintStream#join(UniConstraintStream, BiJoiner)}
     * with a proper {@link BiJoiner} predicate (such as {@link Joiners#equal(Function, Function)},
     * because the latter applies hashing and/or indexing, so it doesn't create every combination just to filter it out.
     *
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
     * Important: {@link TriConstraintStream#filter(TriPredicate)} Filtering} this is slower and less scalable
     * than a {@link #join(UniConstraintStream, TriJoiner)},
     * because it doesn't apply hashing and/or indexing on the properties,
     * so it creates and checks every combination of [A, B] and C.
     *
     * @param otherStream never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every combination of [A, B] and C
     */
    default <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream) {
        return join(otherStream, new NoneTriJoiner<>());
    }

    /**
     * Create a new {@link TriConstraintStream} for every combination of [A, B] and C for which the {@link TriJoiner}
     * is true (for the properties it extracts from both facts).
     * <p>
     * Important: This is faster and more scalable than a {@link #join(UniConstraintStream) join}
     * followed by a {@link TriConstraintStream#filter(TriPredicate) filter},
     * because it applies hashing and/or indexing on the properties,
     * so it doesn't create nor checks every combination of [A, B] and C.
     *
     * @param otherStream never null
     * @param joiner never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every combination of [A, B] and C for which the {@link TriFunction} is
     *         true
     */
    <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream, TriJoiner<A, B, C> joiner);

    /**
     * Create a new {@link TriConstraintStream} for every combination of [A, B] and C.
     * <p>
     * Important: {@link TriConstraintStream#filter(TriPredicate)} Filtering} this is slower and less scalable
     * than a {@link #join(Class, TriJoiner)},
     * because it doesn't apply hashing and/or indexing on the properties,
     * so it creates and checks every combination of [A, B] and C.
     * <p>
     * This method is syntactic sugar for {@link #join(UniConstraintStream)}.
     *
     * @param otherClass never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every combination of [A, B] and C
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass) {
        return join(otherClass, new NoneTriJoiner<>());
    }

    /**
     * Create a new {@link TriConstraintStream} for every combination of [A, B] and C for which the {@link TriJoiner}
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
     *
     * @param otherClass never null
     * @param joiner never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every combination of [A, B] and C for which the {@link TriJoiner} is
     *         true
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C> joiner) {
        return join(getConstraintFactory().from(otherClass), joiner);
    }

    /**
     * As defined by {@link #join(Class, TriJoiner)}.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every combination of [A, B] and C for which all the
     *         {@link TriJoiner joiners} are true
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2) {
        return join(otherClass, new TriJoiner[] { joiner1, joiner2 });
    }

    /**
     * As defined by {@link #join(Class, TriJoiner)}.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every combination of [A, B] and C for which all the
     *         {@link TriJoiner joiners} are true
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2, TriJoiner<A, B, C> joiner3) {
        return join(otherClass, new TriJoiner[] { joiner1, joiner2, joiner3 });
    }

    /**
     * As defined by {@link #join(Class, TriJoiner)}.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every combination of [A, B] and C for which all the
     *         {@link TriJoiner joiners} are true
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2, TriJoiner<A, B, C> joiner3, TriJoiner<A, B, C> joiner4) {
        return join(otherClass, new TriJoiner[] { joiner1, joiner2, joiner3, joiner4 });
    }

    /**
     * As defined by {@link #join(Class, TriJoiner)}.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link BiJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiners never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every combination of [A, B] and C for which all the
     *         {@link TriJoiner joiners} are true
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        BiConstraintStreamHelper<A, B, C> helper = new BiConstraintStreamHelper<>(this);
        return helper.join(otherClass, joiners);
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    /**
     * Create a new {@link BiConstraintStream} for every pair of A and B where C exists for which the {@link TriJoiner}
     * is true (for the properties it extracts from the facts).
     * <p>
     * This method has overloaded methods with multiple {@link TriJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiner never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every pair of A and B where C exists for which the {@link TriJoiner}
     *         is true
     */
    default <C> BiConstraintStream<A, B> ifExists(Class<C> otherClass, TriJoiner<A, B, C> joiner) {
        return ifExists(otherClass, new TriJoiner[] { joiner });
    }

    /**
     * As defined by {@link #ifExists(Class, TriJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every pair of A and B where C exists for which the {@link TriJoiner}s
     *         are true
     */
    default <C> BiConstraintStream<A, B> ifExists(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2) {
        return ifExists(otherClass, new TriJoiner[] { joiner1, joiner2 });
    }

    /**
     * As defined by {@link #ifExists(Class, TriJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every pair of A and B where C exists for which the {@link TriJoiner}s
     *         are true
     */
    default <C> BiConstraintStream<A, B> ifExists(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2, TriJoiner<A, B, C> joiner3) {
        return ifExists(otherClass, new TriJoiner[] { joiner1, joiner2, joiner3 });
    }

    /**
     * As defined by {@link #ifExists(Class, TriJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every pair of A and B where C exists for which the {@link TriJoiner}s
     *         are true
     */
    default <C> BiConstraintStream<A, B> ifExists(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2, TriJoiner<A, B, C> joiner3, TriJoiner<A, B, C> joiner4) {
        return ifExists(otherClass, new TriJoiner[] { joiner1, joiner2, joiner3, joiner4 });
    }

    /**
     * As defined by {@link #ifExists(Class, TriJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link TriJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiners never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every pair of A and B where C exists for which the {@link TriJoiner}s
     *         are true
     */
    <C> BiConstraintStream<A, B> ifExists(Class<C> otherClass, TriJoiner<A, B, C>... joiners);

    /**
     * Create a new {@link BiConstraintStream} for every pair of A and B where C does not exist for which the
     * {@link TriJoiner} is true (for the properties it extracts from the facts).
     * <p>
     * This method has overloaded methods with multiple {@link TriJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiner never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every pair of A and B where C does not exist for which the
     *         {@link TriJoiner} is true
     */
    default <C> BiConstraintStream<A, B> ifNotExists(Class<C> otherClass, TriJoiner<A, B, C> joiner) {
        return ifNotExists(otherClass, new TriJoiner[] { joiner });
    }

    /**
     * As defined by {@link #ifNotExists(Class, TriJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every pair of A and B where C does not exist for which the
     *         {@link TriJoiner}s are true
     */
    default <C> BiConstraintStream<A, B> ifNotExists(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2) {
        return ifNotExists(otherClass, new TriJoiner[] { joiner1, joiner2 });
    }

    /**
     * As defined by {@link #ifNotExists(Class, TriJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every pair of A and B where C does not exist for which the
     *         {@link TriJoiner}s are true
     */
    default <C> BiConstraintStream<A, B> ifNotExists(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2, TriJoiner<A, B, C> joiner3) {
        return ifNotExists(otherClass, new TriJoiner[] { joiner1, joiner2, joiner3 });
    }

    /**
     * As defined by {@link #ifNotExists(Class, TriJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every pair of A and B where C does not exist for which the
     *         {@link TriJoiner}s are true
     */
    default <C> BiConstraintStream<A, B> ifNotExists(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2, TriJoiner<A, B, C> joiner3, TriJoiner<A, B, C> joiner4) {
        return ifNotExists(otherClass, new TriJoiner[] { joiner1, joiner2, joiner3, joiner4 });
    }

    /**
     * As defined by {@link #ifNotExists(Class, TriJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link TriJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiners never null
     * @param <C> the type of the third matched fact
     * @return never null, a stream that matches every pair of A and B where C does not exist for which the
     *         {@link TriJoiner}s are true
     */
    <C> BiConstraintStream<A, B> ifNotExists(Class<C> otherClass, TriJoiner<A, B, C>... joiners);

    // ************************************************************************
    // Group by
    // ************************************************************************

    /**
     * Runs all tuples of the stream through a given @{@link BiConstraintCollector} and converts them into a new
     * {@link UniConstraintStream} which only has a single tuple, the result of applying {@link BiConstraintCollector}.
     *
     * @param collector never null, the collector to perform the grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <Result_> the type of a fact in the destination {@link UniConstraintStream}'s tuple
     * @return never null
     */
    <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector);

    /**
     * Convert the {@link BiConstraintStream} to a {@link UniConstraintStream}, containing the set of tuples resulting
     * from applying the group key mapping function on all tuples of the original stream.
     * Neither tuple of the new stream {@link Objects#equals(Object, Object)} any other.
     *
     * @param groupKeyMapping never null, mapping function to convert each element in the stream to a different element
     * @param <GroupKey_> the type of a fact in the destination {@link UniConstraintStream}'s tuple
     * @return never null
     */
    <GroupKey_> UniConstraintStream<GroupKey_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping);

    /**
     * Convert the {@link BiConstraintStream} to a different {@link BiConstraintStream}, consisting of unique tuples.
     * <p>
     * The first fact is the return value of the first group key mapping function, applied on the incoming tuple.
     * The second fact is the return value of a given {@link BiConstraintCollector} applied on all incoming tuples with
     * the same first fact.
     *
     * @param groupKeyMapping never null, function to convert the fact in the original tuple to a different fact
     * @param collector never null, the collector to perform the grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <GroupKey_> the type of the first fact in the destination {@link BiConstraintStream}'s tuple
     * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <Result_> the type of the second fact in the destination {@link BiConstraintStream}'s tuple
     * @return never null
     */
    <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            BiFunction<A, B, GroupKey_> groupKeyMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector);

    /**
     * Convert the {@link BiConstraintStream} to a different {@link BiConstraintStream}, consisting of unique tuples.
     * <p>
     * The first fact is the return value of the first group key mapping function, applied on the incoming tuple.
     * The second fact is the return value of the second group key mapping function, applied on all incoming tuples with
     * the same first fact.
     *
     * @param groupKeyAMapping never null, function to convert the facts in the original tuple to a new fact
     * @param groupKeyBMapping never null, function to convert the facts in the original tuple to another new fact
     * @param <GroupKeyA_> the type of the first fact in the destination {@link BiConstraintStream}'s tuple
     * @param <GroupKeyB_> the type of the second fact in the destination {@link BiConstraintStream}'s tuple
     * @return never null
     */
    <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping);

    /**
     * Combines the semantics of {@link #groupBy(BiFunction, BiFunction)} and {@link #groupBy(BiConstraintCollector)}.
     * That is, the first and second facts in the tuple follow the {@link #groupBy(BiFunction, BiFunction)} semantics,
     * and the third fact is the result of applying {@link BiConstraintCollector#finisher()} on all the tuples of the
     * original {@link UniConstraintStream} that belong to the group.
     *
     * @param groupKeyAMapping never null, function to convert the original tuple into a first fact
     * @param groupKeyBMapping never null, function to convert the original tuple into a second fact
     * @param collector never null, the collector to perform the grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <GroupKeyA_> the type of the first fact in the destination {@link TriConstraintStream}'s tuple
     * @param <GroupKeyB_> the type of the second fact in the destination {@link TriConstraintStream}'s tuple
     * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <Result_> the type of the third fact in the destination {@link TriConstraintStream}'s tuple
     * @return never null
     */
    <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector);

    /**
     * Combines the semantics of {@link #groupBy(BiFunction, BiFunction)} and {@link #groupBy(BiConstraintCollector)}.
     * That is, the first and second facts in the tuple follow the {@link #groupBy(BiFunction, BiFunction)} semantics.
     * The third fact is the result of applying the first {@link BiConstraintCollector#finisher()} on all the tuples
     * of the original {@link BiConstraintStream} that belong to the group.
     * The fourth fact is the result of applying the second {@link BiConstraintCollector#finisher()} on all the tuples
     * of the original {@link BiConstraintStream} that belong to the group
     *
     * @param groupKeyAMapping never null, function to convert the original tuple into a first fact
     * @param groupKeyBMapping never null, function to convert the original tuple into a second fact
     * @param collectorC never null, the collector to perform the first grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param collectorD never null, the collector to perform the second grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <GroupKeyA_> the type of the first fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <GroupKeyB_> the type of the second fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <ResultContainerC_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultC_> the type of the third fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <ResultContainerD_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultD_> the type of the fourth fact in the destination {@link QuadConstraintStream}'s tuple
     * @return never null
     */
    <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD);

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeLong(String, Score, ToLongBiFunction)} or
     * {@link #penalizeBigDecimal(String, Score, BiFunction)} instead.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalize(String constraintName, Score<?> constraintWeight, ToIntBiFunction<A, B> matchWeigher) {
        return penalize(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #penalize(String, Score, ToIntBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntBiFunction<A, B> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeLong(String constraintName, Score<?> constraintWeight,
            ToLongBiFunction<A, B> matchWeigher) {
        return penalizeLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #penalizeLong(String, Score, ToLongBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongBiFunction<A, B> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeBigDecimal(String constraintName, Score<?> constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return penalizeBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeBigDecimal(String, Score, BiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeConfigurableLong(String, ToLongBiFunction)} or
     * {@link #penalizeConfigurableBigDecimal(String, BiFunction)} instead.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurable(String constraintName, ToIntBiFunction<A, B> matchWeigher) {
        return penalizeConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurable(String, ToIntBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurable(String constraintPackage, String constraintName,
            ToIntBiFunction<A, B> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurableLong(String constraintName, ToLongBiFunction<A, B> matchWeigher) {
        return penalizeConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurableLong(String, ToLongBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurableBigDecimal(String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return penalizeConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurableBigDecimal(String, BiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardLong(String, Score, ToLongBiFunction)} or
     * {@link #rewardBigDecimal(String, Score, BiFunction)} instead.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint reward(String constraintName, Score<?> constraintWeight, ToIntBiFunction<A, B> matchWeigher) {
        return reward(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #reward(String, Score, ToIntBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntBiFunction<A, B> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardLong(String constraintName, Score<?> constraintWeight,
            ToLongBiFunction<A, B> matchWeigher) {
        return rewardLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #rewardLong(String, Score, ToLongBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongBiFunction<A, B> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardBigDecimal(String constraintName, Score<?> constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return rewardBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #rewardBigDecimal(String, Score, BiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardConfigurableLong(String, ToLongBiFunction)} or
     * {@link #rewardConfigurableBigDecimal(String, BiFunction)} instead.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurable(String constraintName, ToIntBiFunction<A, B> matchWeigher) {
        return rewardConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurable(String, ToIntBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurable(String constraintPackage, String constraintName, ToIntBiFunction<A, B> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurableLong(String constraintName, ToLongBiFunction<A, B> matchWeigher) {
        return rewardConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurableLong(String, ToLongBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurableBigDecimal(String constraintName, BiFunction<A, B, BigDecimal> matchWeigher) {
        return rewardConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurableBigDecimal(String, BiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher);

    /**
     * Positively or negatively impact the {@link Score} by the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #impact(String, Score)}.
     * <p>
     * Use {@code penalize(...)} or {@code reward(...)} instead, unless this constraint can both have positive and
     * negative weights.
     * <p>
     * For non-int {@link Score} types use {@link #impactLong(String, Score, ToLongBiFunction)} or
     * {@link #impactBigDecimal(String, Score, BiFunction)} instead.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint impact(String constraintName, Score<?> constraintWeight, ToIntBiFunction<A, B> matchWeigher) {
        return impact(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #impact(String, Score, ToIntBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impact(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntBiFunction<A, B> matchWeigher);

    /**
     * Positively or negatively impact the {@link Score} by the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #impact(String, Score)}.
     * <p>
     * Use {@code penalizeLong(...)} or {@code rewardLong(...)} instead, unless this constraint can both have positive
     * and negative weights.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint impactLong(String constraintName, Score<?> constraintWeight,
            ToLongBiFunction<A, B> matchWeigher) {
        return impactLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #impactLong(String, Score, ToLongBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impactLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongBiFunction<A, B> matchWeigher);

    /**
     * Positively or negatively impact the {@link Score} by the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #impact(String, Score)}.
     * <p>
     * Use {@code penalizeBigDecimal(...)} or {@code rewardBigDecimal(...)} instead, unless this constraint can both
     * have positive and negative weights.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint impactBigDecimal(String constraintName, Score<?> constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return impactBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #impactBigDecimal(String, Score, BiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impactBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher);

    /**
     * Positively or negatively impact the {@link Score} by the {@link ConstraintWeight} multiplied by the match weight.
     * <p>
     * Use {@code penalizeConfigurable(...)} or {@code rewardConfigurable(...)} instead, unless this constraint can both
     * have positive and negative weights.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the
     * {@link ConstraintConfiguration}, so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #impact(String, Score)} instead.
     * <p>
     * The {@link Constraint#getConstraintPackage()} defaults to {@link ConstraintConfiguration#constraintPackage()}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint impactConfigurable(String constraintName, ToIntBiFunction<A, B> matchWeigher) {
        return impactConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #impactConfigurable(String, ToIntBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impactConfigurable(String constraintPackage, String constraintName,
            ToIntBiFunction<A, B> matchWeigher);

    /**
     * Positively or negatively impact the {@link Score} by the {@link ConstraintWeight} multiplied by the match weight.
     * <p>
     * Use {@code penalizeConfigurableLong(...)} or {@code rewardConfigurableLong(...)} instead, unless this constraint
     * can both have positive and negative weights.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the
     * {@link ConstraintConfiguration}, so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #impact(String, Score)} instead.
     * <p>
     * The {@link Constraint#getConstraintPackage()} defaults to {@link ConstraintConfiguration#constraintPackage()}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint impactConfigurableLong(String constraintName, ToLongBiFunction<A, B> matchWeigher) {
        return impactConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #impactConfigurableLong(String, ToLongBiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impactConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher);

    /**
     * Positively or negatively impact the {@link Score} by the {@link ConstraintWeight} multiplied by the match weight.
     * <p>
     * Use {@code penalizeConfigurableBigDecimal(...)} or {@code rewardConfigurableBigDecimal(...)} instead, unless this
     * constraint can both have positive and negative weights.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the
     * {@link ConstraintConfiguration}, so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #impact(String, Score)} instead.
     * <p>
     * The {@link Constraint#getConstraintPackage()} defaults to {@link ConstraintConfiguration#constraintPackage()}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint impactConfigurableBigDecimal(String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher) {
        return impactConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #impactConfigurableBigDecimal(String, BiFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impactConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher);

}
