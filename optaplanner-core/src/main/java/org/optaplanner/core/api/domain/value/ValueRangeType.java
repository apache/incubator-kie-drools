/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.api.domain.value;

import org.optaplanner.core.impl.solution.Solution;

public enum ValueRangeType {
    /**
     * The planning value range for the planning variable is defined by a property on the {@link Solution}.
     */
    FROM_SOLUTION_PROPERTY,
    /**
     * The planning value range for the planning variable is defined by a property on the planning entity.
     */
    FROM_PLANNING_ENTITY_PROPERTY,
    /**
     * The planning value range for the planning variable is undefined.
     * This is incompatible with several optimization algorithms.
     */
    UNDEFINED
}
