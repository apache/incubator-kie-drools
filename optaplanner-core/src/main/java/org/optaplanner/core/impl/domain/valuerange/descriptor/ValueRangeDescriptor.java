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

package org.optaplanner.core.impl.domain.valuerange.descriptor;

import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.variable.descriptor.PlanningVariableDescriptor;
import org.optaplanner.core.impl.solution.Solution;

public interface ValueRangeDescriptor {

    /**
     * @return never null
     */
    PlanningVariableDescriptor getVariableDescriptor();

    /**
     * @return true if the {@link ValueRange} is countable
     * (for example a double value range between 1.2 and 1.4 is not countable)
     */
    boolean isCountable();

    /**
     * If this method return true, this instance is safe to cast to {@link EntityIndependentValueRangeDescriptor}.
     * @return true if the {@link ValueRange} is the same for all entities of the same solution
     */
    boolean isEntityIndependent();

    /**
     * @param solution never null
     * @param entity never null. To avoid this parameter,
     * use {@link EntityIndependentValueRangeDescriptor#extractValueRange(Solution)} instead.
     * @return never null
     */
    ValueRange<?> extractValueRange(Solution solution, Object entity);

}
