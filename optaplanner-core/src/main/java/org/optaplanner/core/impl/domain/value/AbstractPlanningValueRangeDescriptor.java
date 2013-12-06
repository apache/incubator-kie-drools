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

package org.optaplanner.core.impl.domain.value;

import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.api.domain.value.composite.NullableValueRange;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;

public abstract class AbstractPlanningValueRangeDescriptor implements PlanningValueRangeDescriptor {

    protected final PlanningVariableDescriptor variableDescriptor;
    protected final boolean addNullInValueRange;

    public AbstractPlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor,
            boolean addNullInValueRange) {
        this.variableDescriptor = variableDescriptor;
        this.addNullInValueRange = addNullInValueRange;
    }

    @Override
    public PlanningVariableDescriptor getVariableDescriptor() {
        return variableDescriptor;
    }

    protected <T> ValueRange<T> doNullInValueRangeWrapping(ValueRange<T> valueRange) {
        if (addNullInValueRange) {
            valueRange = new NullableValueRange<T>(valueRange);
        }
        return valueRange;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getVariableName() + ")";
    }

}
