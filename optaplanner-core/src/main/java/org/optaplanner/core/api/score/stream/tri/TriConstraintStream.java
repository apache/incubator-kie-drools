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

package org.optaplanner.core.api.score.stream.tri;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.quad.NoneQuadJoiner;
import org.optaplanner.core.impl.score.stream.tri.TriConstraintStreamHelper;

/**
 * A {@link ConstraintStream} that matches three facts.
 *
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
     * Important: This is slower and less scalable than {@link BiConstraintStream#join(UniConstraintStream, TriJoiner)}
     * with a proper {@link TriJoiner} predicate (such as {@link Joiners#equal(BiFunction, Function)},
     * because the latter applies hashing and/or indexing, so it doesn't create every combination just to filter it out.
     *
     * @param predicate never null
     * @return never null
     */
    TriConstraintStream<A, B, C> filter(TriPredicate<A, B, C> predicate);

    // ************************************************************************
    // Join
    // ************************************************************************

    /**
     * Create a new {@link QuadConstraintStream} for every combination of [A, B, C] and D.
     * <p>
     * Important: {@link QuadConstraintStream#filter(QuadPredicate) Filtering} this is slower and less scalable
     * than a {@link #join(UniConstraintStream, QuadJoiner)},
     * because it doesn't apply hashing and/or indexing on the properties,
     * so it creates and checks every combination of [A, B] and C.
     *
     * @param otherStream never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every combination of [A, B, C] and D
     */
    default <D> QuadConstraintStream<A, B, C, D> join(UniConstraintStream<D> otherStream) {
        return join(otherStream, new NoneQuadJoiner<>());
    }

    /**
     * Create a new {@link QuadConstraintStream} for every combination of [A, B] and C for which the {@link QuadJoiner}
     * is true (for the properties it extracts from all facts).
     * <p>
     * Important: This is faster and more scalable than a {@link #join(UniConstraintStream) join}
     * followed by a {@link QuadConstraintStream#filter(QuadPredicate) filter},
     * because it applies hashing and/or indexing on the properties,
     * so it doesn't create nor checks every combination of [A, B, C] and D.
     *
     * @param otherStream never null
     * @param joiner never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every combination of [A, B, C] and D for which the {@link QuadJoiner}
     *         is true
     */
    <D> QuadConstraintStream<A, B, C, D> join(UniConstraintStream<D> otherStream, QuadJoiner<A, B, C, D> joiner);

    /**
     * Create a new {@link QuadConstraintStream} for every combination of [A, B, C] and D.
     * <p>
     * Important: {@link QuadConstraintStream#filter(QuadPredicate)} Filtering} this is slower and less scalable
     * than a {@link #join(Class, QuadJoiner)},
     * because it doesn't apply hashing and/or indexing on the properties,
     * so it creates and checks every combination of [A, B, C] and D.
     * <p>
     * This method is syntactic sugar for {@link #join(UniConstraintStream)}.
     *
     * @param otherClass never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every combination of [A, B, C] and D
     */
    default <D> QuadConstraintStream<A, B, C, D> join(Class<D> otherClass) {
        return join(otherClass, new NoneQuadJoiner<>());
    }

    /**
     * Create a new {@link QuadConstraintStream} for every combination of [A, B, C] and D for which the
     * {@link QuadJoiner} is true (for the properties it extracts from all facts).
     * <p>
     * Important: This is faster and more scalable than a {@link #join(Class, QuadJoiner) join}
     * followed by a {@link QuadConstraintStream#filter(QuadPredicate) filter},
     * because it applies hashing and/or indexing on the properties,
     * so it doesn't create nor checks every combination of [A, B, C] and D.
     * <p>
     * This method is syntactic sugar for {@link #join(UniConstraintStream, QuadJoiner)}.
     * <p>
     * This method has overloaded methods with multiple {@link QuadJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiner never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every combination of [A, B, C] and D for which the {@link QuadJoiner}
     *         is true
     */
    default <D> QuadConstraintStream<A, B, C, D> join(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner) {
        return join(getConstraintFactory().from(otherClass), joiner);
    }

    /**
     * As defined by {@link #join(Class, QuadJoiner)}.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every combination of [A, B, C] and D for which all the
     *         {@link QuadJoiner joiners} are true
     */
    default <D> QuadConstraintStream<A, B, C, D> join(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner1,
            QuadJoiner<A, B, C, D> joiner2) {
        return join(otherClass, new QuadJoiner[] { joiner1, joiner2 });
    }

    /**
     * As defined by {@link #join(Class, QuadJoiner)}.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every combination of [A, B, C] and D for which all the
     *         {@link QuadJoiner joiners} are true
     */
    default <D> QuadConstraintStream<A, B, C, D> join(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner1,
            QuadJoiner<A, B, C, D> joiner2, QuadJoiner<A, B, C, D> joiner3) {
        return join(otherClass, new QuadJoiner[] { joiner1, joiner2, joiner3 });
    }

    /**
     * As defined by {@link #join(Class, QuadJoiner)}.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every combination of [A, B, C] and D for which all the
     *         {@link QuadJoiner joiners} are true
     */
    default <D> QuadConstraintStream<A, B, C, D> join(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner1,
            QuadJoiner<A, B, C, D> joiner2, QuadJoiner<A, B, C, D> joiner3, QuadJoiner<A, B, C, D> joiner4) {
        return join(otherClass, new QuadJoiner[] { joiner1, joiner2, joiner3, joiner4 });
    }

    /**
     * As defined by {@link #join(Class, QuadJoiner)}.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link QuadJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiners never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every combination of [A, B, C] and D for which all the
     *         {@link QuadJoiner joiners} are true
     */
    default <D> QuadConstraintStream<A, B, C, D> join(Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners) {
        TriConstraintStreamHelper<A, B, C, D> helper = new TriConstraintStreamHelper<>(this);
        return helper.join(otherClass, joiners);
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    /**
     * Create a new {@link BiConstraintStream} for every tuple of A, B and C where D exists for which the
     * {@link QuadJoiner} is true (for the properties it extracts from the facts).
     * <p>
     * This method has overloaded methods with multiple {@link QuadJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiner never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every tuple of A, B and C where D exists for which the
     *         {@link QuadJoiner} is true
     */
    default <D> TriConstraintStream<A, B, C> ifExists(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner) {
        return ifExists(otherClass, new QuadJoiner[] { joiner });
    }

    /**
     * As defined by {@link #ifExists(Class, QuadJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every tuple of A, B and C where D exists for which the
     *         {@link QuadJoiner}s are true
     */
    default <D> TriConstraintStream<A, B, C> ifExists(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner1,
            QuadJoiner<A, B, C, D> joiner2) {
        return ifExists(otherClass, new QuadJoiner[] { joiner1, joiner2 });
    }

    /**
     * As defined by {@link #ifExists(Class, QuadJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every tuple of A, B and C where D exists for which the
     *         {@link QuadJoiner}s are true
     */
    default <D> TriConstraintStream<A, B, C> ifExists(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner1,
            QuadJoiner<A, B, C, D> joiner2, QuadJoiner<A, B, C, D> joiner3) {
        return ifExists(otherClass, new QuadJoiner[] { joiner1, joiner2, joiner3 });
    }

    /**
     * As defined by {@link #ifExists(Class, QuadJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every tuple of A, B and C where D exists for which the
     *         {@link QuadJoiner}s are true
     */
    default <D> TriConstraintStream<A, B, C> ifExists(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner1,
            QuadJoiner<A, B, C, D> joiner2, QuadJoiner<A, B, C, D> joiner3, QuadJoiner<A, B, C, D> joiner4) {
        return ifExists(otherClass, new QuadJoiner[] { joiner1, joiner2, joiner3, joiner4 });
    }

    /**
     * As defined by {@link #ifExists(Class, QuadJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link QuadJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiners never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every tuple of A, B and C where D exists for which the
     *         {@link QuadJoiner}s are true
     */
    <D> TriConstraintStream<A, B, C> ifExists(Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners);

    /**
     * Create a new {@link BiConstraintStream} for every tuple of A, B and C where D does not exist for which the
     * {@link QuadJoiner} is true (for the properties it extracts from the facts).
     * <p>
     * This method has overloaded methods with multiple {@link QuadJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiner never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every tuple of A, B and C where D does not exist for which the
     *         {@link QuadJoiner} is true
     */
    default <D> TriConstraintStream<A, B, C> ifNotExists(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner) {
        return ifNotExists(otherClass, new QuadJoiner[] { joiner });
    }

    /**
     * As defined by {@link #ifNotExists(Class, QuadJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every tuple of A, B and C where D does not exist for which the
     *         {@link QuadJoiner}s are true
     */
    default <D> TriConstraintStream<A, B, C> ifNotExists(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner1,
            QuadJoiner<A, B, C, D> joiner2) {
        return ifNotExists(otherClass, new QuadJoiner[] { joiner1, joiner2 });
    }

    /**
     * As defined by {@link #ifNotExists(Class, QuadJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every tuple of A, B and C where D does not exist for which the
     *         {@link QuadJoiner}s are true
     */
    default <D> TriConstraintStream<A, B, C> ifNotExists(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner1,
            QuadJoiner<A, B, C, D> joiner2, QuadJoiner<A, B, C, D> joiner3) {
        return ifNotExists(otherClass, new QuadJoiner[] { joiner1, joiner2, joiner3 });
    }

    /**
     * As defined by {@link #ifNotExists(Class, QuadJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <D> the type of the fourth matched fact
     * @return never null, a stream that matches every tuple of A, B and C where D does not exist for which the
     *         {@link QuadJoiner}s are true
     */
    default <D> TriConstraintStream<A, B, C> ifNotExists(Class<D> otherClass, QuadJoiner<A, B, C, D> joiner1,
            QuadJoiner<A, B, C, D> joiner2, QuadJoiner<A, B, C, D> joiner3, QuadJoiner<A, B, C, D> joiner4) {
        return ifNotExists(otherClass, new QuadJoiner[] { joiner1, joiner2, joiner3, joiner4 });
    }

    /**
     * As defined by {@link #ifNotExists(Class, QuadJoiner)}. For performance reasons, indexing joiners must be placed
     * before filtering joiners.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link QuadJoiner} parameters.
     *
     * @param <D> the type of the fourth matched fact
     * @param otherClass never null
     * @param joiners never null
     * @return never null, a stream that matches every tuple of A, B and C where D does not exist for which the
     *         {@link QuadJoiner}s are true
     */
    <D> TriConstraintStream<A, B, C> ifNotExists(Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners);

    // ************************************************************************
    // Group by
    // ************************************************************************

    /**
     * Convert the {@link TriConstraintStream} to a {@link UniConstraintStream}, containing only a single tuple, the
     * result of applying {@link TriConstraintCollector}.
     *
     * @param collector never null, the collector to perform the grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <Result_> the type of a fact in the destination {@link UniConstraintStream}'s tuple
     * @return never null
     */
    <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector);

    /**
     * Convert the {@link TriConstraintStream} to a {@link UniConstraintStream}, containing the set of tuples resulting
     * from applying the group key mapping function on all tuples of the original stream.
     * Neither tuple of the new stream {@link Objects#equals(Object, Object)} any other.
     *
     * @param groupKeyMapping never null, mapping function to convert each element in the stream to a different element
     * @param <GroupKey_> the type of a fact in the destination {@link UniConstraintStream}'s tuple
     * @return never null
     */
    <GroupKey_> UniConstraintStream<GroupKey_> groupBy(TriFunction<A, B, C, GroupKey_> groupKeyMapping);

    /**
     * Convert the {@link TriConstraintStream} to a {@link BiConstraintStream}, consisting of unique tuples.
     * <p>
     * The first fact is the return value of the first group key mapping function, applied on the incoming tuple.
     * The second fact is the return value of a given {@link TriConstraintCollector} applied on all incoming tuples with
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
            TriFunction<A, B, C, GroupKey_> groupKeyMapping,
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector);

    /**
     * Convert the {@link TriConstraintStream} to a {@link BiConstraintStream}, consisting of unique tuples.
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
            TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping, TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping);

    /**
     * Combines the semantics of {@link #groupBy(TriFunction, TriFunction)} and {@link #groupBy(TriConstraintCollector)}.
     * That is, the first and second facts in the tuple follow the {@link #groupBy(TriFunction, TriFunction)} semantics,
     * and the third fact is the result of applying {@link TriConstraintCollector#finisher()} on all the tuples of the
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
            TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping, TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector);

    /**
     * Combines the semantics of {@link #groupBy(TriFunction, TriFunction)} and {@link #groupBy(TriConstraintCollector)}.
     * That is, the first and second facts in the tuple follow the {@link #groupBy(TriFunction, TriFunction)} semantics.
     * The third fact is the result of applying the first {@link TriConstraintCollector#finisher()} on all the tuples
     * of the original {@link TriConstraintStream} that belong to the group.
     * The fourth fact is the result of applying the second {@link TriConstraintCollector#finisher()} on all the tuples
     * of the original {@link TriConstraintStream} that belong to the group
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
                    TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping, TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> collectorC,
                    TriConstraintCollector<A, B, C, ResultContainerD_, ResultD_> collectorD);

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeLong(String, Score, ToLongTriFunction)} or
     * {@link #penalizeBigDecimal(String, Score, TriFunction)} instead.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalize(String constraintName, Score<?> constraintWeight,
            ToIntTriFunction<A, B, C> matchWeigher) {
        return penalize(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #penalize(String, Score, ToIntTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntTriFunction<A, B, C> matchWeigher);

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
            ToLongTriFunction<A, B, C> matchWeigher) {
        return penalizeLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #penalizeLong(String, Score, ToLongTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongTriFunction<A, B, C> matchWeigher);

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
            TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return penalizeBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeBigDecimal(String, Score, TriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            TriFunction<A, B, C, BigDecimal> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeConfigurableLong(String, ToLongTriFunction)} or
     * {@link #penalizeConfigurableBigDecimal(String, TriFunction)} instead.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurable(String constraintName, ToIntTriFunction<A, B, C> matchWeigher) {
        return penalizeConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurable(String, ToIntTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurable(String constraintPackage, String constraintName,
            ToIntTriFunction<A, B, C> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurableLong(String constraintName, ToLongTriFunction<A, B, C> matchWeigher) {
        return penalizeConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurableLong(String, ToLongTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurableLong(String constraintPackage, String constraintName,
            ToLongTriFunction<A, B, C> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurableBigDecimal(String constraintName,
            TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return penalizeConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurableBigDecimal(String, TriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurableBigDecimal(String constraintPackage, String constraintName,
            TriFunction<A, B, C, BigDecimal> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardLong(String, Score, ToLongTriFunction)} or
     * {@link #rewardBigDecimal(String, Score, TriFunction)} instead.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint reward(String constraintName, Score<?> constraintWeight,
            ToIntTriFunction<A, B, C> matchWeigher) {
        return reward(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #reward(String, Score, ToIntTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntTriFunction<A, B, C> matchWeigher);

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
            ToLongTriFunction<A, B, C> matchWeigher) {
        return rewardLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #rewardLong(String, Score, ToLongTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongTriFunction<A, B, C> matchWeigher);

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
            TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return rewardBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #rewardBigDecimal(String, Score, TriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            TriFunction<A, B, C, BigDecimal> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardConfigurableLong(String, ToLongTriFunction)} or
     * {@link #rewardConfigurableBigDecimal(String, TriFunction)} instead.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurable(String constraintName, ToIntTriFunction<A, B, C> matchWeigher) {
        return rewardConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurable(String, ToIntTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurable(String constraintPackage, String constraintName,
            ToIntTriFunction<A, B, C> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurableLong(String constraintName, ToLongTriFunction<A, B, C> matchWeigher) {
        return rewardConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurableLong(String, ToLongTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurableLong(String constraintPackage, String constraintName,
            ToLongTriFunction<A, B, C> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurableBigDecimal(String constraintName,
            TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return rewardConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurableBigDecimal(String, TriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurableBigDecimal(String constraintPackage, String constraintName,
            TriFunction<A, B, C, BigDecimal> matchWeigher);

    /**
     * Positively or negatively impact the {@link Score} by the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #impact(String, Score)}.
     * <p>
     * Use {@code penalize(...)} or {@code reward(...)} instead, unless this constraint can both have positive and
     * negative weights.
     * <p>
     * For non-int {@link Score} types use {@link #impactLong(String, Score, ToLongTriFunction)} or
     * {@link #impactBigDecimal(String, Score, TriFunction)} instead.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint impact(String constraintName, Score<?> constraintWeight,
            ToIntTriFunction<A, B, C> matchWeigher) {
        return impact(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #impact(String, Score, ToIntTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impact(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntTriFunction<A, B, C> matchWeigher);

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
            ToLongTriFunction<A, B, C> matchWeigher) {
        return impactLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight,
                matchWeigher);
    }

    /**
     * As defined by {@link #impactLong(String, Score, ToLongTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impactLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongTriFunction<A, B, C> matchWeigher);

    /**
     * Positively or negatively impact the {@link Score} by the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #impact(String, Score)}.
     * <p>
     * Use {@code penalizeBigDecimal(...)} or {@code rewardBigDecimal(...)} unless you intend to mix positive and
     * negative weights.
     *
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint impactBigDecimal(String constraintName, Score<?> constraintWeight,
            TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return impactBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #impactBigDecimal(String, Score, TriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impactBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            TriFunction<A, B, C, BigDecimal> matchWeigher);

    /**
     * Positively or negatively impact the {@link Score} by the {@link ConstraintWeight} for each match.
     * <p>
     * Use {@code penalizeConfigurable(...)} or {@code rewardConfigurable(...)} instead, unless this constraint can both
     * have positive and negative weights.
     * <p>
     * For non-int {@link Score} types use {@link #impactConfigurableLong(String, ToLongTriFunction)} or
     * {@link #impactConfigurableBigDecimal(String, TriFunction)} instead.
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
    default Constraint impactConfigurable(String constraintName, ToIntTriFunction<A, B, C> matchWeigher) {
        return impactConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #impactConfigurable(String, ToIntTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impactConfigurable(String constraintPackage, String constraintName,
            ToIntTriFunction<A, B, C> matchWeigher);

    /**
     * Positively or negatively impact the {@link Score} by the {@link ConstraintWeight} for each match.
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
    default Constraint impactConfigurableLong(String constraintName, ToLongTriFunction<A, B, C> matchWeigher) {
        return impactConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #impactConfigurableLong(String, ToLongTriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impactConfigurableLong(String constraintPackage, String constraintName,
            ToLongTriFunction<A, B, C> matchWeigher);

    /**
     * Positively or negatively impact the {@link Score} by the {@link ConstraintWeight} for each match.
     * <p>
     * Use {@code penalizeConfigurableBigDecimal(...)} or {@code rewardConfigurableLong(...)} instead, unless this
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
            TriFunction<A, B, C, BigDecimal> matchWeigher) {
        return impactConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName,
                matchWeigher);
    }

    /**
     * As defined by {@link #impactConfigurableBigDecimal(String, TriFunction)}.
     *
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint impactConfigurableBigDecimal(String constraintPackage, String constraintName,
            TriFunction<A, B, C, BigDecimal> matchWeigher);
}
