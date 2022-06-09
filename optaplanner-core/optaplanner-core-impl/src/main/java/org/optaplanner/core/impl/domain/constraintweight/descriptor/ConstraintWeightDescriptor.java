package org.optaplanner.core.impl.domain.constraintweight.descriptor;

import java.util.Objects;
import java.util.function.Function;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ConstraintWeightDescriptor<Solution_> {

    private final ConstraintConfigurationDescriptor<Solution_> constraintConfigurationDescriptor;

    private final String constraintPackage;
    private final String constraintName;
    private final MemberAccessor memberAccessor;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public ConstraintWeightDescriptor(ConstraintConfigurationDescriptor<Solution_> constraintConfigurationDescriptor,
            MemberAccessor memberAccessor) {
        this.constraintConfigurationDescriptor = constraintConfigurationDescriptor;
        ConstraintWeight constraintWeightAnnotation = memberAccessor.getAnnotation(ConstraintWeight.class);
        String constraintPackage = constraintWeightAnnotation.constraintPackage();
        if (constraintPackage.isEmpty()) {
            // If a @ConstraintConfiguration extends a @ConstraintConfiguration, their constraintPackage might differ.
            ConstraintConfiguration constraintConfigurationAnnotation = memberAccessor.getDeclaringClass()
                    .getAnnotation(ConstraintConfiguration.class);
            if (constraintConfigurationAnnotation == null) {
                throw new IllegalStateException("Impossible state: " + ConstraintConfigurationDescriptor.class.getSimpleName()
                        + " only reflects over members with a @" + ConstraintConfiguration.class.getSimpleName()
                        + " annotation.");
            }
            constraintPackage = constraintConfigurationAnnotation.constraintPackage();
            if (constraintPackage.isEmpty()) {
                Package pack = memberAccessor.getDeclaringClass().getPackage();
                constraintPackage = (pack == null) ? "" : pack.getName();
            }
        }
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintWeightAnnotation.value();
        this.memberAccessor = memberAccessor;
    }

    public String getConstraintPackage() {
        return constraintPackage;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public MemberAccessor getMemberAccessor() {
        return memberAccessor;
    }

    public Function<Solution_, Score<?>> createExtractor() {
        SolutionDescriptor<Solution_> solutionDescriptor = constraintConfigurationDescriptor.getSolutionDescriptor();
        MemberAccessor constraintConfigurationMemberAccessor = solutionDescriptor.getConstraintConfigurationMemberAccessor();
        return (Solution_ solution) -> {
            Object constraintConfiguration = Objects.requireNonNull(
                    constraintConfigurationMemberAccessor.executeGetter(solution),
                    "Constraint configuration provider (" + constraintConfigurationMemberAccessor +
                            ") returns null.");
            return (Score<?>) memberAccessor.executeGetter(constraintConfiguration);
        };
    }

    @Override
    public String toString() {
        return constraintPackage + ":" + constraintName;
    }

}
