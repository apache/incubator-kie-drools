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

import org.optaplanner.constraint.streams.common.bi.BiJoinerComber;
import org.optaplanner.constraint.streams.common.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.common.uni.InnerUniConstraintStream;
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
        BiJoinerComber<A, A> joinerComber = BiJoinerComber.comb(joiners);
        joinerComber.addJoiner(buildLessThanId(sourceClass));
        return ((InnerUniConstraintStream<A>) forEach(sourceClass))
                .join(forEach(sourceClass), joinerComber);
    }

    private <A> DefaultBiJoiner<A, A> buildLessThanId(Class<A> sourceClass) {
        MemberAccessor planningIdMemberAccessor =
                ConfigUtils.findPlanningIdMemberAccessor(sourceClass, getSolutionDescriptor().getDomainAccessType(),
                        getSolutionDescriptor().getGeneratedMemberAccessorMap());
        if (planningIdMemberAccessor == null) {
            throw new IllegalArgumentException("The fromClass (" + sourceClass + ") has no member with a @"
                    + PlanningId.class.getSimpleName() + " annotation,"
                    + " so the pairs cannot be made unique ([A,B] vs [B,A]).");
        }
        Function<A, Comparable> planningIdGetter = planningIdMemberAccessor.getGetterFunction();
        return (DefaultBiJoiner<A, A>) lessThan(planningIdGetter);
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
        BiJoinerComber<A, A> joinerComber = BiJoinerComber.comb(joiners);
        joinerComber.addJoiner(buildLessThanId(fromClass));
        return ((InnerUniConstraintStream<A>) from(fromClass))
                .join(from(fromClass), joinerComber);
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
