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
