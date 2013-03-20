/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.domain.value;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;

import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.solution.Solution;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Specifies which planning values can be used for a planning variable.
 * This is specified on a getter of a java bean property which already has a {@link PlanningVariable} annotation.
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface ValueRange {

    /**
     * This is commonly {@link ValueRangeType#FROM_SOLUTION_PROPERTY}.
     * @return never null
     */
    ValueRangeType type();

    /**
     * The property name for which exists a getter on the {@link Solution} that returns a {@link Collection}.
     * @return never null for {@link ValueRangeType#FROM_SOLUTION_PROPERTY}, always null otherwise.
     */
    String solutionProperty() default "";

    /**
     * The property name for which exists a getter on the planning entity that returns a {@link Collection}.
     * @return never null for {@link ValueRangeType#FROM_PLANNING_ENTITY_PROPERTY}, always null otherwise.
     */
    String planningEntityProperty() default "";

    /**
     * @return never true for {@link ValueRangeType#UNDEFINED}
     */
    // TODO support specific-variable-uninitialized + document in manual
    boolean excludeUninitializedPlanningEntity() default false;

}
