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

package org.optaplanner.core.impl.score.stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.stream.bi.FilteringBiJoiner;

public abstract class InnerConstraintFactory<Solution_> implements ConstraintFactory {

    // ************************************************************************
    // from
    // ************************************************************************

    @Override
    public <A> UniConstraintStream<A> from(Class<A> fromClass) {
        UniConstraintStream<A> stream = fromUnfiltered(fromClass);
        EntityDescriptor<Solution_> entityDescriptor = getSolutionDescriptor().findEntityDescriptor(fromClass);
        if (entityDescriptor != null && entityDescriptor.hasAnyGenuineVariables()) {
            Predicate<A> predicate = (Predicate<A>) entityDescriptor.getIsInitializedPredicate();
            stream = stream.filter(predicate);
        }
        return stream;
    }

    @Override
    public <A> BiConstraintStream<A, A> fromUniquePair(Class<A> fromClass, BiJoiner<A, A> joiner) {
        MemberAccessor planningIdMemberAccessor = ConfigUtils.findPlanningIdMemberAccessor(fromClass);
        if (planningIdMemberAccessor == null) {
            throw new IllegalArgumentException("The fromClass (" + fromClass + ") has no member with a @"
                    + PlanningId.class.getSimpleName() + " annotation,"
                    + " so the pairs cannot be made unique ([A,B] vs [B,A]).");
        }
        // TODO In Bavet breaks node sharing + involves unneeded indirection
        Function<A, Comparable> planningIdGetter = fact -> (Comparable<?>) planningIdMemberAccessor.executeGetter(fact);
        // Joiner.filtering() must come last, yet Bavet requires that Joiner.lessThan() be last. This is a workaround.
        if (joiner instanceof FilteringBiJoiner) {
            BiPredicate<A, A> filter = ((FilteringBiJoiner<A, A>) joiner).getFilter();
            return from(fromClass)
                    .join(fromClass, lessThan(planningIdGetter))
                    .filter(filter);
        } else {
            return from(fromClass)
                    .join(fromClass, joiner, lessThan(planningIdGetter));
        }
    }

    public <A> void assertValidFromType(Class<A> fromType) {
        SolutionDescriptor<Solution_> solutionDescriptor = getSolutionDescriptor();
        Stream<Class> entityClassStream = solutionDescriptor.getEntityDescriptors().stream()
                .map(EntityDescriptor::getEntityClass);
        Stream<Class> factClassStream = solutionDescriptor.getProblemFactMemberAccessorMap()
                .values()
                .stream()
                .map(MemberAccessor::getType);
        Stream<Class> factCollectionClassStream = solutionDescriptor.getProblemFactCollectionMemberAccessorMap()
                .entrySet()
                .stream()
                .map(entry -> {
                    MemberAccessor accessor = entry.getValue();
                    return ConfigUtils.extractCollectionGenericTypeParameter("solutionClass",
                            solutionDescriptor.getSolutionClass(), accessor.getType(), accessor.getGenericType(),
                            ProblemFactCollectionProperty.class, entry.getKey());
                });
        Set<Class> allAcceptedClassSet = concat(concat(entityClassStream, factClassStream), factCollectionClassStream)
                .collect(toSet());
        /*
         * Need to support the following situations:
         * 1/ FactType == FromType; querying for the declared type.
         * 2/ FromType extends/implements FactType; querying for impl type where declared type is its interface.
         * 3/ FromType super FactType; querying for interface where declared type is its implementation.
         */
        boolean hasMatchingType = allAcceptedClassSet.stream()
                .anyMatch(factType -> fromType.isAssignableFrom(factType) || factType.isAssignableFrom(fromType));
        if (!hasMatchingType) {
            List<String> canonicalClassNameList = allAcceptedClassSet.stream()
                    .map(Class::getCanonicalName)
                    .sorted()
                    .collect(toList());
            throw new IllegalArgumentException("Cannot use class (" + fromType.getCanonicalName()
                    + ") in a constraint stream as it is neither the same as, nor a superclass or superinterface of "
                    + "one of planning entities or problem facts.\n"
                    + "Ensure that all from(), join(), ifExists() and ifNotExists() building blocks only reference "
                    + "classes assignable from planning entities or problem facts (" + canonicalClassNameList + ") "
                    + "annotated on the planning solution (" + solutionDescriptor.getSolutionClass().getCanonicalName()
                    + ").");
        }
    }

    // ************************************************************************
    // SessionFactory creation
    // ************************************************************************

    /**
     * This method is thread-safe.
     *
     * @param constraints never null
     * @return never null
     */
    public abstract ConstraintSessionFactory<Solution_> buildSessionFactory(Constraint[] constraints);

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    /**
     * @return never null
     */
    public abstract SolutionDescriptor<Solution_> getSolutionDescriptor();

}
