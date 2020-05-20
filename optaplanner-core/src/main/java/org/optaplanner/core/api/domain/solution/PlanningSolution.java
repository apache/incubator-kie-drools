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

package org.optaplanner.core.api.domain.solution;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.autodiscover.AutoDiscoverMemberType;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;

/**
 * Specifies that the class is a planning solution.
 * A solution represents a problem and a possible solution of that problem.
 * A possible solution does not need to be optimal or even feasible.
 * A solution's planning variables might not be initialized (especially when delivered as a problem).
 * <p>
 * A solution is mutable.
 * For scalability reasons (to facilitate incremental score calculation),
 * the same solution instance (called the working solution per move thread) is continuously modified.
 * It's cloned to recall the best solution.
 * <p>
 * Each planning solution must have exactly 1 {@link PlanningScore} property.
 * <p>
 * Each planning solution must have at least 1 {@link PlanningEntityCollectionProperty}
 * or {@link PlanningEntityProperty} property.
 * <p>
 * Each planning solution is recommended to have 1 {@link ConstraintConfigurationProvider} property too.
 * <p>
 * Each planning solution used with Drools score calculation must have at least 1 {@link ProblemFactCollectionProperty}
 * or {@link ProblemFactProperty} property.
 * <p>
 * The class should have a public no-arg constructor, so it can be cloned
 * (unless the {@link #solutionCloner()} is specified).
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface PlanningSolution {

    /**
     * Enable reflection through the members of the class
     * to automatically assume {@link PlanningScore}, {@link PlanningEntityCollectionProperty},
     * {@link PlanningEntityProperty}, {@link ProblemFactCollectionProperty}, {@link ProblemFactProperty}
     * and {@link ConstraintConfigurationProvider} annotations based on the member type.
     *
     * @return never null
     */
    AutoDiscoverMemberType autoDiscoverMemberType() default AutoDiscoverMemberType.NONE;

    /**
     * Overrides the default {@link SolutionCloner} to implement a custom {@link PlanningSolution} cloning implementation.
     * <p>
     * If this is not specified, then the default reflection-based {@link SolutionCloner} is used,
     * so you don't have to worry about it.
     *
     * @return {@link NullSolutionCloner} when it is null (workaround for annotation limitation)
     */
    Class<? extends SolutionCloner> solutionCloner() default NullSolutionCloner.class;

    /** Workaround for annotation limitation in {@link #solutionCloner()}. */
    interface NullSolutionCloner extends SolutionCloner {
    }

    /**
     * @return never null
     */
    LookUpStrategyType lookUpStrategyType() default LookUpStrategyType.PLANNING_ID_OR_NONE;

}
