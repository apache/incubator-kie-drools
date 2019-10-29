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
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;
import org.optaplanner.core.impl.score.stream.tri.NoneTriJoiner;

/**
 * A {@link ConstraintStream} that matches two facts.
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
     * Important: This is slower and less scalable than {@link UniConstraintStream#join(UniConstraintStream, BiJoiner[])}
     * with a proper {@link BiJoiner} predicate (such as {@link Joiners#equal(Function, Function)},
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
    default <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream) {
        return join(otherStream, new NoneTriJoiner<>());
    }

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
        return join(otherClass, new NoneTriJoiner<>());
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
        return join(getConstraintFactory().from(otherClass), joiner);
    }

    /**
     * As defined by {@link #join(Class, TriJoiner)}.
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <C> the type of the third matched fact
     * @return a stream that matches every combination of [A, B] and C for which all the {@link BiJoiner joiners} are true
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2) {
        return join(otherClass, AbstractTriJoiner.merge(joiner1, joiner2));
    }

    /**
     * As defined by {@link #join(Class, TriJoiner)}.
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <C> the type of the third matched fact
     * @return a stream that matches every combination of [A, B] and C for which all the {@link BiJoiner joiners} are true
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2, TriJoiner<A, B, C> joiner3) {
        return join(otherClass, AbstractTriJoiner.merge(joiner1, joiner2, joiner3));
    }

    /**
     * As defined by {@link #join(Class, TriJoiner)}.
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <C> the type of the third matched fact
     * @return a stream that matches every combination of [A, B] and C for which all the {@link BiJoiner joiners} are true
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C> joiner1,
            TriJoiner<A, B, C> joiner2, TriJoiner<A, B, C> joiner3, TriJoiner<A, B, C> joiner4) {
        return join(otherClass, AbstractTriJoiner.merge(joiner1, joiner2, joiner3, joiner4));
    }

    /**
     * As defined by {@link #join(Class, TriJoiner)}.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link BiJoiner} parameters.
     * @param otherClass never null
     * @param joiners never null
     * @param <C> the type of the third matched fact
     * @return a stream that matches every combination of [A, B] and C for which all the {@link BiJoiner joiners} are true
     */
    default <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        return join(otherClass, AbstractTriJoiner.merge(joiners));
    }

    // ************************************************************************
    // Group by (TODO implement these)
    // ************************************************************************

    /*
    <GroupKey_> UniConstraintStream<GroupKey_> groupBy(
            BiFunction<A, B, GroupKey_> groupKeyMapping);

    <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            BiFunction<A, B, GroupKey_> groupKeyMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector);

    <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping,
            BiFunction<A, B, GroupKeyB_> groupKeyBMapping);

    <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_>
    TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping,
            BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector);
     */

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeLong(String, Score, ToLongBiFunction)} or {@link #penalizeBigDecimal(String, Score, BiFunction)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalize(String constraintName, Score<?> constraintWeight, ToIntBiFunction<A, B> matchWeigher) {
        return penalize(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #penalize(String, Score, ToIntBiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight, ToIntBiFunction<A, B> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeLong(String constraintName, Score<?> constraintWeight, ToLongBiFunction<A, B> matchWeigher) {
        return penalizeLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeLong(String, Score, ToLongBiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeLong(String constraintPackage, String constraintName, Score<?> constraintWeight, ToLongBiFunction<A, B> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeBigDecimal(String constraintName, Score<?> constraintWeight, BiFunction<A, B, BigDecimal> matchWeigher) {
        return penalizeBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeBigDecimal(String, Score, BiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight, BiFunction<A, B, BigDecimal> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeConfigurableLong(String, ToLongBiFunction)} or {@link #penalizeConfigurableBigDecimal(String, BiFunction)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurable(String constraintName, ToIntBiFunction<A, B> matchWeigher) {
        return penalizeConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurable(String, ToIntBiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurable(String constraintPackage, String constraintName, ToIntBiFunction<A, B> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurableLong(String constraintName, ToLongBiFunction<A, B> matchWeigher) {
        return penalizeConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurableLong(String, ToLongBiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurableLong(String constraintPackage, String constraintName, ToLongBiFunction<A, B> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint penalizeConfigurableBigDecimal(String constraintName, BiFunction<A, B, BigDecimal> matchWeigher) {
        return penalizeConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #penalizeConfigurableBigDecimal(String, BiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint penalizeConfigurableBigDecimal(String constraintPackage, String constraintName, BiFunction<A, B, BigDecimal> matchWeigher);


    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardLong(String, Score, ToLongBiFunction)} or {@link #rewardBigDecimal(String, Score, BiFunction)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint reward(String constraintName, Score<?> constraintWeight, ToIntBiFunction<A, B> matchWeigher) {
        return reward(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #reward(String, Score, ToIntBiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight, ToIntBiFunction<A, B> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardLong(String constraintName, Score<?> constraintWeight, ToLongBiFunction<A, B> matchWeigher) {
        return rewardLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #rewardLong(String, Score, ToLongBiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardLong(String constraintPackage, String constraintName, Score<?> constraintWeight, ToLongBiFunction<A, B> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardBigDecimal(String constraintName, Score<?> constraintWeight, BiFunction<A, B, BigDecimal> matchWeigher) {
        return rewardBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight, matchWeigher);
    }

    /**
     * As defined by {@link #rewardBigDecimal(String, Score, BiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight, BiFunction<A, B, BigDecimal> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardConfigurableLong(String, ToLongBiFunction)} or {@link #rewardConfigurableBigDecimal(String, BiFunction)} instead.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurable(String constraintName, ToIntBiFunction<A, B> matchWeigher) {
        return rewardConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurable(String, ToIntBiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurable(String constraintPackage, String constraintName, ToIntBiFunction<A, B> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurableLong(String constraintName, ToLongBiFunction<A, B> matchWeigher) {
        return rewardConfigurableLong(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurableLong(String, ToLongBiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurableLong(String constraintPackage, String constraintName, ToLongBiFunction<A, B> matchWeigher);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    default Constraint rewardConfigurableBigDecimal(String constraintName, BiFunction<A, B, BigDecimal> matchWeigher) {
        return rewardConfigurableBigDecimal(getConstraintFactory().getDefaultConstraintPackage(), constraintName, matchWeigher);
    }

    /**
     * As defined by {@link #rewardConfigurableBigDecimal(String, BiFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    Constraint rewardConfigurableBigDecimal(String constraintPackage, String constraintName, BiFunction<A, B, BigDecimal> matchWeigher);

}

