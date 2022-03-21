/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import java.util.function.BiPredicate;
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
            return forEachUniquePair(sourceClass, merge(joiners));
        }
        // Merge indexing joiners, create stream and append filters for every subsequent filtering joiner.
        BiJoiner<A, A> mergedJoiner = merge(Arrays.copyOf(joiners, indexOfFirstFilter));
        BiConstraintStream<A, A> resultingStream = forEachUniquePair(sourceClass, mergedJoiner);
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

    @SafeVarargs
    public static <A, B> BiJoiner<A, B> merge(BiJoiner<A, B>... joiners) {
        int joinerCount = joiners.length;
        if (joinerCount == 0) {
            return DefaultBiJoiner.NONE;
        } else if (joinerCount == 1) {
            return joiners[0];
        }
        BiJoiner<A, B> result = joiners[0];
        for (int i = 1; i < joinerCount; i++) {
            result = result.and(joiners[i]);
        }
        return result;
    }

    @Override
    public <A> BiConstraintStream<A, A> forEachUniquePair(Class<A> sourceClass, BiJoiner<A, A> joiner) {
        MemberAccessor planningIdMemberAccessor =
                ConfigUtils.findPlanningIdMemberAccessor(sourceClass, getSolutionDescriptor().getDomainAccessType(),
                        getSolutionDescriptor().getGeneratedMemberAccessorMap());
        if (planningIdMemberAccessor == null) {
            throw new IllegalArgumentException("The fromClass (" + sourceClass + ") has no member with a @"
                    + PlanningId.class.getSimpleName() + " annotation,"
                    + " so the pairs cannot be made unique ([A,B] vs [B,A]).");
        }
        // TODO In Bavet breaks node sharing + involves unneeded indirection
        Function<A, Comparable> planningIdGetter = fact -> (Comparable<?>) planningIdMemberAccessor.executeGetter(fact);
        // Joiner.filtering() must come last, yet Bavet requires that Joiner.lessThan() be last. This is a workaround.
        if (joiner instanceof FilteringBiJoiner) {
            BiPredicate<A, A> filter = ((FilteringBiJoiner<A, A>) joiner).getFilter();
            return forEach(sourceClass)
                    .join(sourceClass, lessThan(planningIdGetter))
                    .filter(filter);
        } else {
            return forEach(sourceClass)
                    .join(sourceClass, joiner, lessThan(planningIdGetter));
        }
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
            return fromUniquePair(fromClass, merge(joiners));
        }
        // Merge indexing joiners, create stream and append filters for every subsequent filtering joiner.
        BiJoiner<A, A> mergedJoiner = merge(Arrays.copyOf(joiners, indexOfFirstFilter));
        BiConstraintStream<A, A> resultingStream = fromUniquePair(fromClass, mergedJoiner);
        for (int filterIndex = indexOfFirstFilter; filterIndex < joiners.length; filterIndex++) {
            FilteringBiJoiner<A, A> filteringJoiner = (FilteringBiJoiner<A, A>) joiners[filterIndex];
            resultingStream = resultingStream.filter(filteringJoiner.getFilter());
        }
        return resultingStream;
    }

    @Override
    public <A> BiConstraintStream<A, A> fromUniquePair(Class<A> fromClass, BiJoiner<A, A> joiner) {
        MemberAccessor planningIdMemberAccessor =
                ConfigUtils.findPlanningIdMemberAccessor(fromClass, getSolutionDescriptor().getDomainAccessType(),
                        getSolutionDescriptor().getGeneratedMemberAccessorMap());
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
