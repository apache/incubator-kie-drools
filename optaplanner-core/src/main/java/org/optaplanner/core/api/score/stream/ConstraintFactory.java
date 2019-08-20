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

package org.optaplanner.core.api.score.stream;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.bi.NoneBiJoiner;

/**
 * WARNING: The ConstraintStreams/ConstraintProvider API is TECH PREVIEW.
 * It works but it has many API gaps.
 * Therefore, it is not rich enough yet to handle complex constraints.
 * <p>
 * The factory to create every {@link ConstraintStream} (for example with {@link #from(Class)})
 * which ends in a {@link Constraint} returned by {@link ConstraintProvider#defineConstraints(ConstraintFactory)}.
 */
public interface ConstraintFactory {

    /**
     * This is {@link ConstraintConfiguration#constraintPackage()} if available,
     * otherwise the package of the {@link PlanningSolution} class.
     * @return never null
     */
    String getDefaultConstraintPackage();

    // ************************************************************************
    // from
    // ************************************************************************

    /**
     * Start a {@link ConstraintStream} of all instances of the fromClass
     * that are known as {@link ProblemFactCollectionProperty problem facts} or {@link PlanningEntity planning entities}.
     * <p>
     * If the fromClass is a {@link PlanningEntity}, then it will be automatically
     * {@link UniConstraintStream#filter(Predicate) filtered} to only contain fully initialized entities,
     * for which each genuine {@link PlanningVariable} (of the fromClass or a superclass thereof) is initialized
     * (so when the value is not null - unless {@link PlanningVariable#nullable()} is modified).
     * This filtering will NOT automatically apply to genuine planning variables of subclass planning entities of the fromClass.
     * @param fromClass never null
     * @param <A> the type of the matched problem fact or {@link PlanningEntity planning entity}
     * @return never null
     */
    <A> UniConstraintStream<A> from(Class<A> fromClass);

    /**
     * Like {@link #from(Class)},
     * but without any filtering of uninitialized {@link PlanningEntity planning entities}.
     * @param fromClass never null
     * @param <A> the type of the matched problem fact or {@link PlanningEntity planning entity}
     * @return never null
     */
    <A> UniConstraintStream<A> fromUnfiltered(Class<A> fromClass);

    // ************************************************************************
    // fromUniquePair
    // ************************************************************************

    /**
     * Create a new {@link BiConstraintStream} for every unique combination of A and another A with a higher {@link PlanningId}.
     * <p>
     * Important: {@link BiConstraintStream#filter(BiPredicate) Filtering} this is slower and less scalable
     * than using a {@link #fromUniquePair(Class, BiJoiner) joiner},
     * because it does barely applies hashing and/or indexing on the properties,
     * so it creates and checks almost every combination of A and A.
     * <p>
     * This method is syntactic sugar for {@link UniConstraintStream#join(Class)}.
     * It automatically adds a {@link Joiners#lessThan(Function) lessThan} joiner on the {@link PlanningId} of A.
     * @param fromClass never null
     * @param <A> the type of the matched problem fact or {@link PlanningEntity planning entity}
     * @return a stream that matches every unique combination of A and another A
     */
    default <A> BiConstraintStream<A, A> fromUniquePair(Class<A> fromClass) {
        return fromUniquePair(fromClass, new NoneBiJoiner<>());
    }

    /**
     * Create a new {@link BiConstraintStream} for every unique combination of A and another A with a higher {@link PlanningId}
     * for which the {@link BiJoiner} is true (for the properties it extracts from both facts).
     * <p>
     * Important: This is faster and more scalable than not using a {@link #fromUniquePair(Class)} joiner}
     * followed by a {@link BiConstraintStream#filter(BiPredicate) filter},
     * because it applies hashing and/or indexing on the properties,
     * so it doesn't create nor checks almost every combination of A and A.
     * <p>
     * This method is syntactic sugar for {@link UniConstraintStream#join(Class, BiJoiner)}.
     * It automatically adds a {@link Joiners#lessThan(Function) lessThan} joiner on the {@link PlanningId} of A.
     * <p>
     * This method has overloaded methods with multiple {@link BiJoiner} parameters.
     * @param fromClass never null
     * @param joiner never null
     * @param <A> the type of the matched problem fact or {@link PlanningEntity planning entity}
     * @return a stream that matches every unique combination of A and another A for which the {@link BiJoiner} is true
     */
    <A> BiConstraintStream<A, A> fromUniquePair(Class<A> fromClass, BiJoiner<A, A> joiner);

    /**
     * As defined by {@link #fromUniquePair(Class, BiJoiner)}.
     * @param fromClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <A> the type of the matched problem fact or {@link PlanningEntity planning entity}
     * @return a stream that matches every unique combination of A and another A for which all the {@link BiJoiner joiners} are true
     */
    default <A> BiConstraintStream<A, A> fromUniquePair(Class<A> fromClass, BiJoiner<A, A> joiner1, BiJoiner<A, A> joiner2) {
        return fromUniquePair(fromClass, AbstractBiJoiner.merge(joiner1, joiner2));
    }

    /**
     * As defined by {@link #fromUniquePair(Class, BiJoiner)}.
     * @param fromClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <A> the type of the matched problem fact or {@link PlanningEntity planning entity}
     * @return a stream that matches every unique combination of A and another A for which all the {@link BiJoiner joiners} are true
     */
    default <A> BiConstraintStream<A, A> fromUniquePair(Class<A> fromClass, BiJoiner<A, A> joiner1, BiJoiner<A, A> joiner2,
            BiJoiner<A, A> joiner3) {
        return fromUniquePair(fromClass, AbstractBiJoiner.merge(joiner1, joiner2, joiner3));
    }

    /**
     * As defined by {@link #fromUniquePair(Class, BiJoiner)}.
     * @param fromClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <A> the type of the matched problem fact or {@link PlanningEntity planning entity}
     * @return a stream that matches every unique combination of A and another A for which all the {@link BiJoiner joiners} are true
     */
    default <A> BiConstraintStream<A, A> fromUniquePair(Class<A> fromClass, BiJoiner<A, A> joiner1, BiJoiner<A, A> joiner2,
            BiJoiner<A, A> joiner3, BiJoiner<A, A> joiner4) {
        return fromUniquePair(fromClass, AbstractBiJoiner.merge(joiner1, joiner2, joiner3, joiner4));
    }

    /**
     * As defined by {@link #fromUniquePair(Class, BiJoiner)}.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link BiJoiner} parameters.
     * @param fromClass never null
     * @param joiners never null
     * @param <A> the type of the matched problem fact or {@link PlanningEntity planning entity}
     * @return a stream that matches every unique combination of A and another A for which all the {@link BiJoiner joiners} are true
     */
    default <A> BiConstraintStream<A, A> fromUniquePair(Class<A> fromClass, BiJoiner<A, A>... joiners) {
        return fromUniquePair(fromClass, AbstractBiJoiner.merge(joiners));
    }

}
