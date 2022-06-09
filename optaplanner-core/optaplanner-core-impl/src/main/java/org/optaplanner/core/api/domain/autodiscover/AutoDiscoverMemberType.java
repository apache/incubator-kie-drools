package org.optaplanner.core.api.domain.autodiscover;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;

/**
 * Determines if and how to automatically presume
 * {@link ConstraintConfigurationProvider}, {@link ProblemFactCollectionProperty}, {@link ProblemFactProperty},
 * {@link PlanningEntityCollectionProperty}, {@link PlanningEntityProperty} and {@link PlanningScore} annotations
 * on {@link PlanningSolution} members based from the member type.
 */
public enum AutoDiscoverMemberType {
    /**
     * Do not reflect.
     */
    NONE,
    /**
     * Reflect over the fields and automatically behave as the appropriate annotation is there
     * based on the field type.
     */
    FIELD,
    /**
     * Reflect over the getter methods and automatically behave as the appropriate annotation is there
     * based on the return type.
     */
    GETTER;
}
