/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.common;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.optaplanner.constraint.streams.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.bi.FilteringBiJoiner;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

public abstract class InnerConstraintFactory<Solution_, Constraint_ extends Constraint> implements ConstraintFactory {

    @Override
    public <A> UniConstraintStream<A> forEach(Class<A> sourceClass) {
        UniConstraintStream<A> stream = forEachIncludingNullVars(sourceClass);
        Predicate<A> nullityFilter = getNullityFilter(sourceClass);
        return nullityFilter == null ? stream : stream.filter(nullityFilter);
    }

    public <A> Predicate<A> getNullityFilter(Class<A> fromClass) {
        EntityDescriptor<Solution_> entityDescriptor = getSolutionDescriptor().findEntityDescriptor(fromClass);
        if (entityDescriptor != null && entityDescriptor.hasAnyGenuineVariables()) {
            return (Predicate<A>) entityDescriptor.getHasNoNullVariables();
        }
        return null;
    }

    @Override
    public <A> BiConstraintStream<A, A> forEachUniquePair(Class<A> sourceClass, BiJoiner<A, A>... joiners) {
        // First make sure filtering joiners are always last, if there are any.
        int indexOfFirstFilter = findIndexOfFirstFilteringJoiner(joiners);
        if (indexOfFirstFilter < 0) {
            // No filtering joiners. Simply merge all joiners and create the stream.
            DefaultBiJoiner<A, A>[] indexingJoiners = Arrays.asList(joiners).toArray(new DefaultBiJoiner[0]);
            return innerForEachUniquePair(sourceClass, DefaultBiJoiner.merge(indexingJoiners));
        }
        // Create stream using indexing joiners and append filters for every subsequent filtering joiner.
        DefaultBiJoiner<A, A>[] indexingJoiners = Arrays.asList(Arrays.copyOf(joiners, indexOfFirstFilter))
                .toArray(new DefaultBiJoiner[0]);
        BiConstraintStream<A, A> resultingStream = innerForEachUniquePair(sourceClass, indexingJoiners);
        for (int filterIndex = indexOfFirstFilter; filterIndex < joiners.length; filterIndex++) {
            FilteringBiJoiner<A, A> filteringJoiner = (FilteringBiJoiner<A, A>) joiners[filterIndex];
            resultingStream = resultingStream.filter(filteringJoiner.getFilter());
        }
        return resultingStream;
    }

    @SafeVarargs
    private static <A> int findIndexOfFirstFilteringJoiner(BiJoiner<A, A>... joiners) {
        int indexOfFirstFilter = -1;
        for (int index = 0; index < joiners.length; index++) {
            boolean seenFilterAlready = indexOfFirstFilter >= 0;
            BiJoiner<A, A> joiner = joiners[index];
            boolean isFilter = joiner instanceof FilteringBiJoiner;
            if (!seenFilterAlready && isFilter) {
                indexOfFirstFilter = index;
                continue;
            }
            if (seenFilterAlready && !isFilter) {
                throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow " +
                        "a filtering joiner (" + joiners[indexOfFirstFilter] + ").\n" +
                        "Maybe reorder the joiners such that filtering() joiners are later in the parameter list.");
            }
        }
        return indexOfFirstFilter;
    }

    private <A> BiConstraintStream<A, A> innerForEachUniquePair(Class<A> sourceClass, DefaultBiJoiner<A, A>... joiner) {
        MemberAccessor planningIdMemberAccessor =
                ConfigUtils.findPlanningIdMemberAccessor(sourceClass, getSolutionDescriptor().getDomainAccessType(),
                        getSolutionDescriptor().getGeneratedMemberAccessorMap());
        if (planningIdMemberAccessor == null) {
            throw new IllegalArgumentException("The fromClass (" + sourceClass + ") has no member with a @"
                    + PlanningId.class.getSimpleName() + " annotation,"
                    + " so the pairs cannot be made unique ([A,B] vs [B,A]).");
        }
        Function<A, Comparable> planningIdGetter = planningIdMemberAccessor.getGetterFunction();
        // Bavet requires that Joiner.lessThan() be last.
        return forEach(sourceClass)
                .join(sourceClass, DefaultBiJoiner.merge(joiner), lessThan(planningIdGetter));
    }

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
    public <A> BiConstraintStream<A, A> fromUniquePair(Class<A> fromClass, BiJoiner<A, A>... joiners) {
        // First make sure filtering joiners are always last, if there are any.
        int indexOfFirstFilter = findIndexOfFirstFilteringJoiner(joiners);
        if (indexOfFirstFilter < 0) {
            // No filtering joiners. Simply merge all joiners and create the stream.
            DefaultBiJoiner<A, A>[] indexingJoiners = Arrays.asList(joiners).toArray(new DefaultBiJoiner[0]);
            return innerFromUniquePair(fromClass, DefaultBiJoiner.merge(indexingJoiners));
        }
        // Create stream using indexing joiners and append filters for every subsequent filtering joiner.
        DefaultBiJoiner<A, A>[] indexingJoiners = Arrays.asList(Arrays.copyOf(joiners, indexOfFirstFilter))
                .toArray(new DefaultBiJoiner[0]);
        BiConstraintStream<A, A> resultingStream = innerFromUniquePair(fromClass, indexingJoiners);
        for (int filterIndex = indexOfFirstFilter; filterIndex < joiners.length; filterIndex++) {
            FilteringBiJoiner<A, A> filteringJoiner = (FilteringBiJoiner<A, A>) joiners[filterIndex];
            resultingStream = resultingStream.filter(filteringJoiner.getFilter());
        }
        return resultingStream;
    }

    private <A> BiConstraintStream<A, A> innerFromUniquePair(Class<A> fromClass, DefaultBiJoiner<A, A>... joiners) {
        MemberAccessor planningIdMemberAccessor =
                ConfigUtils.findPlanningIdMemberAccessor(fromClass, getSolutionDescriptor().getDomainAccessType(),
                        getSolutionDescriptor().getGeneratedMemberAccessorMap());
        if (planningIdMemberAccessor == null) {
            throw new IllegalArgumentException("The fromClass (" + fromClass + ") has no member with a @"
                    + PlanningId.class.getSimpleName() + " annotation,"
                    + " so the pairs cannot be made unique ([A,B] vs [B,A]).");
        }
        Function<A, Comparable> planningIdGetter = planningIdMemberAccessor.getGetterFunction();
        // Bavet requires that Joiner.lessThan() be last.
        return from(fromClass)
                .join(fromClass, DefaultBiJoiner.merge(joiners), lessThan(planningIdGetter));
    }

    public <A> void assertValidFromType(Class<A> fromType) {
        SolutionDescriptor<Solution_> solutionDescriptor = getSolutionDescriptor();
        Set<Class<?>> problemFactOrEntityClassSet = solutionDescriptor.getProblemFactOrEntityClassSet();
        /*
         * Need to support the following situations:
         * 1/ FactType == FromType; querying for the declared type.
         * 2/ FromType extends/implements FactType; querying for impl type where declared type is its interface.
         * 3/ FromType super FactType; querying for interface where declared type is its implementation.
         */
        boolean hasMatchingType = problemFactOrEntityClassSet.stream()
                .anyMatch(factType -> fromType.isAssignableFrom(factType) || factType.isAssignableFrom(fromType));
        if (!hasMatchingType) {
            List<String> canonicalClassNameList = problemFactOrEntityClassSet.stream()
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

    public List<Constraint_> buildConstraints(ConstraintProvider constraintProvider) {
        Constraint[] constraints = constraintProvider.defineConstraints(this);
        if (constraints == null) {
            throw new IllegalStateException("The constraintProvider class (" + constraintProvider.getClass()
                    + ")'s defineConstraints() must not return null.\n"
                    + "Maybe return an empty array instead if there are no constraints.");
        }
        if (Arrays.stream(constraints).anyMatch(Objects::isNull)) {
            throw new IllegalStateException("The constraintProvider class (" + constraintProvider.getClass()
                    + ")'s defineConstraints() must not contain an element that is null.\n"
                    + "Maybe don't include any null elements in the " + Constraint.class.getSimpleName() + " array.");
        }
        // Fail fast on duplicate constraint IDs.
        Map<String, List<Constraint>> constraintsPerIdMap =
                Arrays.stream(constraints).collect(groupingBy(Constraint::getConstraintId));
        constraintsPerIdMap.forEach((constraintId, duplicateConstraintList) -> {
            if (duplicateConstraintList.size() > 1) {
                throw new IllegalStateException("There are multiple constraints with the same ID (" + constraintId + ").");
            }
        });
        return Arrays.stream(constraints)
                .map(c -> (Constraint_) c)
                .collect(Collectors.toList());
    }

    /**
     * @return never null
     */
    public abstract SolutionDescriptor<Solution_> getSolutionDescriptor();

}
