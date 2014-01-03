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

import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.composite.NullableCountableValueRange;
import org.optaplanner.core.impl.domain.variable.descriptor.PlanningVariableDescriptor;

public abstract class AbstractValueRangeDescriptor implements ValueRangeDescriptor {

    protected final PlanningVariableDescriptor variableDescriptor;
    protected final boolean addNullInValueRange;

    public AbstractValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor,
            boolean addNullInValueRange) {
        this.variableDescriptor = variableDescriptor;
        this.addNullInValueRange = addNullInValueRange;
    }

    @Override
    public PlanningVariableDescriptor getVariableDescriptor() {
        return variableDescriptor;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    protected <T> ValueRange<T> doNullInValueRangeWrapping(ValueRange<T> valueRange) {
        if (addNullInValueRange) {
            valueRange = new NullableCountableValueRange<T>((CountableValueRange) valueRange);
        }
        return valueRange;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getVariableName() + ")";
    }

}
