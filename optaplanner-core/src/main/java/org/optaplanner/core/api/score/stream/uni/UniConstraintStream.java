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

package org.optaplanner.core.api.score.stream.uni;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.bi.NoneBiJoiner;

/**
 * A {@link ConstraintStream} that matches one fact.
 * @param <A> the type of the first and only fact in the tuple.
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
    default <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream) {
        return join(otherStream, new NoneBiJoiner<>());
    }

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
        return join(otherClass, new NoneBiJoiner<>());
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
        return join(getConstraintFactory().from(otherClass), joiner);
    }

    /**
     * As defined by {@link #join(Class, BiJoiner)}.
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <B> the type of the second matched fact
     * @return a stream that matches every combination of A and B for which all the {@link BiJoiner joiners} are true
     */
    default <B> BiConstraintStream<A, B> join(Class<B> otherClass, BiJoiner<A, B> joiner1, BiJoiner<A, B> joiner2) {
        return join(otherClass, AbstractBiJoiner.merge(joiner1, joiner2));
    }

    /**
     * As defined by {@link #join(Class, BiJoiner)}.
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <B> the type of the second matched fact
     * @return a stream that matches every combination of A and B for which all the {@link BiJoiner joiners} are true
     */
    default <B> BiConstraintStream<A, B> join(Class<B> otherClass, BiJoiner<A, B> joiner1, BiJoiner<A, B> joiner2,
            BiJoiner<A, B> joiner3) {
        return join(otherClass, AbstractBiJoiner.merge(joiner1, joiner2, joiner3));
    }

    /**
     * As defined by {@link #join(Class, BiJoiner)}.
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <B> the type of the second matched fact
     * @return a stream that matches every combination of A and B for which all the {@link BiJoiner joiners} are true
     */
    default <B> BiConstraintStream<A, B> join(Class<B> otherClass, BiJoiner<A, B> joiner1, BiJoiner<A, B> joiner2,
            BiJoiner<A, B> joiner3, BiJoiner<A, B> joiner4) {
        return join(otherClass, AbstractBiJoiner.merge(joiner1, joiner2, joiner3, joiner4));
    }

    /**
     * As defined by {@link #join(Class, BiJoiner)}.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link BiJoiner} parameters.
     * @param otherClass never null
     * @param joiners never null
     * @param <B> the type of the second matched fact
     * @return a stream that matches every combination of A and B for which all the {@link BiJoiner joiners} are true
     */
    default <B> BiConstraintStream<A, B> join(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        return join(otherClass, AbstractBiJoiner.merge(joiners));
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    /**
     * Runs all tuples of the stream through a given @{@link UniConstraintCollector} and converts them into a new
     * {@link UniConstraintStream} which only has a single tuple, the result of applying {@link UniConstraintCollector}.
     * @param collector never null, the collector to perform the grouping operation with
     * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <Result_> the type of a fact in the destination {@link UniConstraintStream}'s tuple
     * @return never null
     */
    <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            UniConstraintCollector<A, ResultContainer_, Result_> collector);

    /**
     * Convert the {@link UniConstraintStream} to a different {@link UniConstraintStream}, containing of the set of
     * tuples resulting from applying the group key mapping function on all tuples of the original stream.
     * Neither tuple of the new stream will {@link Objects#equals(Object, Object)} any other.
     * @param groupKeyMapping never null, mapping function to convert each element in the stream to a different element
     * @param <GroupKey_> the type of a fact in the destination {@link UniConstraintStream}'s tuple
     * @return never null
     */
    <GroupKey_> UniConstraintStream<GroupKey_> groupBy(Function<A, GroupKey_> groupKeyMapping);

    /**
     * Convert the {@link UniConstraintStream} to a {@link BiConstraintStream}, consisting of tuples which have:
     *
     * <ul>
     *     <li>As the first fact, the value resulting from applying the group key mapping function on the fact from the
     *     original tuple.
     *     Neither such facts of the new stream will {@link Objects#equals(Object, Object)} any other.</li>
     *     <li>As the second fact, the value of applying a given {@link UniConstraintCollector} on all the facts of the
     *     original tuple with a matching group key.</li>
     * </ul>
     * @param groupKeyMapping never null, function to convert a fact in original tuple to a different fact
     * @param <GroupKey_> the type of the first fact in the destination {@link BiConstraintStream}'s tuple
     * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <Result_> the type of the second fact in the destination {@link BiConstraintStream}'s tuple
     * @return never null
     */
    <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            Function<A, GroupKey_> groupKeyMapping,
            UniConstraintCollector<A, ResultContainer_, Result_> collector);

    /*
    /**
     * Convert the {@link UniConstraintStream} to a {@link BiConstraintStream}, consisting of tuples which have:
     *
     * <ul>
     *     <li>As the first fact, the value resulting from applying the "A" group key mapping function to the fact from
     *     the original tuple.</li>
     *     <li>As the second fact, the value resulting from applying the "B" group key mapping function to the fact from
     *     the original tuple, which already passed through the "A" group key mapping.</li>
     * </ul>
     *
     * All bi tuples of the new stream will be unique.
     * @param groupKeyAMapping never null, function to convert first fact in the original tuple to a different fact
     * @param groupKeyBMapping never null, function to convert second fact in the original tuple to a different fact
     * @param <GroupKeyA_> the type of the first fact in the destination {@link BiConstraintStream}'s tuple
     * @param <GroupKeyB_> the type of the second fact in the destination {@link BiConstraintStream}'s tuple
     * @return never null
     */
    // TODO implement this
    //<GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
    //        Function<A, GroupKeyA_> groupKeyAMapping,
    //        Function<A, GroupKeyB_> groupKeyBMapping);

    /*
    /**
     * Combines the semantics of {@link #groupBy(Function, Function)} and {@link #groupBy(UniConstraintCollector)}.
     * That is, the first and second facts in the tuple follow the {@link #groupBy(Function, Function)} semantics, and
     * the third fact is the result of applying {@link UniConstraintCollector#finisher()} on all the tuples of the
     * original {@link UniConstraintStream} that fall in the group.
     * @param groupKeyAMapping never null, function to convert first fact in the original tuple to a different fact
     * @param groupKeyBMapping never null, function to convert second fact in the original tuple to a different fact
     * @param collector never null, the collector to perform the grouping operation with
     * @param <GroupKeyA_> the type of the first fact in the destination {@link TriConstraintStream}'s tuple
     * @param <GroupKeyB_> the type of the second fact in the destination {@link TriConstraintStream}'s tuple
     * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <Result_> the type of the third fact in the destination {@link TriConstraintStream}'s tuple
     * @return never null
     */
    // TODO implement this
    //<GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
    //        Function<A, GroupKeyA_> groupKeyAMapping,
    //        Function<A, GroupKeyB_> groupKeyBMapping,
    //        UniConstraintCollector<A, ResultContainer_, Result_> collector);

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeLong(String, Score, ToLongFunction)} or {@link #penalizeBigDecimal(String, Score, Function)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalize(String constraintName, Score<?> constraintWeight, ToIntFunction<A> matchWeigher) {
        return penalize(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #penalize(String, Score, ToIntFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight, ToIntFunction<A> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeLong(String constraintName, Score<?> constraintWeight, ToLongFunction<A> matchWeigher) {
        return penalizeLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeLong(String, Score, ToLongFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeLong(String constraintPackage, String constraintName, Score<?> constraintWeight, ToLongFunction<A> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeBigDecimal(String constraintName, Score<?> constraintWeight, Function<A, BigDecimal> matchWeigher) {
        return penalizeBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeBigDecimal(String, Score, Function)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight, Function<A, BigDecimal> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeConfigurableLong(String, ToLongFunction)} or {@link #penalizeConfigurableBigDecimal(String, Function)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurable(String constraintName, ToIntFunction<A> matchWeigher) {
        return penalizeConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurable(String, ToIntFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurable(String constraintPackage, String constraintName, ToIntFunction<A> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurableLong(String constraintName, ToLongFunction<A> matchWeigher) {
        return penalizeConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurableLong(String, ToLongFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurableLong(String constraintPackage, String constraintName, ToLongFunction<A> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurableBigDecimal(String constraintName, Function<A, BigDecimal> matchWeigher) {
        return penalizeConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurableBigDecimal(String, Function)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurableBigDecimal(String constraintPackage, String constraintName, Function<A, BigDecimal> matchWeigher);


    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardLong(String, Score, ToLongFunction)} or {@link #rewardBigDecimal(String, Score, Function)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint reward(String constraintName, Score<?> constraintWeight, ToIntFunction<A> matchWeigher) {
        return reward(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #reward(String, Score, ToIntFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight, ToIntFunction<A> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardLong(String constraintName, Score<?> constraintWeight, ToLongFunction<A> matchWeigher) {
        return rewardLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #rewardLong(String, Score, ToLongFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardLong(String constraintPackage, String constraintName, Score<?> constraintWeight, ToLongFunction<A> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardBigDecimal(String constraintName, Score<?> constraintWeight, Function<A, BigDecimal> matchWeigher) {
        return rewardBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #rewardBigDecimal(String, Score, Function)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight, Function<A, BigDecimal> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardConfigurableLong(String, ToLongFunction)} or {@link #rewardConfigurableBigDecimal(String, Function)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurable(String constraintName, ToIntFunction<A> matchWeigher) {
        return rewardConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurable(String, ToIntFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurable(String constraintPackage, String constraintName, ToIntFunction<A> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurableLong(String constraintName, ToLongFunction<A> matchWeigher) {
        return rewardConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurableLong(String, ToLongFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurableLong(String constraintPackage, String constraintName, ToLongFunction<A> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurableBigDecimal(String constraintName, Function<A, BigDecimal> matchWeigher) {
        return rewardConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurableBigDecimal(String, Function)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurableBigDecimal(String constraintPackage, String constraintName, Function<A, BigDecimal> matchWeigher);

}
