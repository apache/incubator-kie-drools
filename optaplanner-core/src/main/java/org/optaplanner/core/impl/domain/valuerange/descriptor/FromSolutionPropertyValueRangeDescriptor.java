/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class FromSolutionPropertyValueRangeDescriptor<Solution_>
        extends AbstractFromPropertyValueRangeDescriptor<Solution_>
        implements EntityIndependentValueRangeDescriptor<Solution_> {

    public FromSolutionPropertyValueRangeDescriptor(
            GenuineVariableDescriptor<Solution_> variableDescriptor, boolean addNullInValueRange,
            MemberAccessor memberAccessor) {
        super(variableDescriptor, addNullInValueRange, memberAccessor);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isEntityIndependent() {
        return true;
    }

    @Override
    public ValueRange<?> extractValueRange(Solution_ solution, Object entity) {
        return readValueRange(solution);
    }

    @Override
    public ValueRange<?> extractValueRange(Solution_ solution) {
        return readValueRange(solution);
    }

}
