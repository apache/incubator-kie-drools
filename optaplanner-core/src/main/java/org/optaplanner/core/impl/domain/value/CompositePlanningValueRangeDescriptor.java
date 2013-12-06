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

package org.optaplanner.core.impl.domain.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.api.domain.value.composite.CompositeValueRange;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.solution.Solution;

public class CompositePlanningValueRangeDescriptor extends AbstractPlanningValueRangeDescriptor
        implements EntityIndependentPlanningValueRangeDescriptor {

    protected final List<PlanningValueRangeDescriptor> valueRangeDescriptorList;
    protected boolean entityIndependent;

    public CompositePlanningValueRangeDescriptor(
            PlanningVariableDescriptor variableDescriptor, boolean addNullInValueRange,
            List<PlanningValueRangeDescriptor> valueRangeDescriptorList) {
        super(variableDescriptor, addNullInValueRange);
        this.valueRangeDescriptorList = valueRangeDescriptorList;
        entityIndependent = true;
        for (PlanningValueRangeDescriptor valueRangeDescriptor : valueRangeDescriptorList) {
            if (!valueRangeDescriptor.isEntityIndependent()) {
                entityIndependent = false;
                break;
            }
        }
    }

    @Override
    public boolean isEntityIndependent() {
        return entityIndependent;
    }

    @Override
    public boolean isValuesCacheable() {
        for (PlanningValueRangeDescriptor valueRangeDescriptor : valueRangeDescriptorList) {
            if (!valueRangeDescriptor.isValuesCacheable()) {
                return false;
            }
        }
        return true;
    }

    public ValueRange<?> extractValueRange(Solution solution, Object entity) {
        List<ValueRange<?>> childValueRangeList = new ArrayList<ValueRange<?>>(valueRangeDescriptorList.size());
        for (PlanningValueRangeDescriptor valueRangeDescriptor : valueRangeDescriptorList) {
            childValueRangeList.add(valueRangeDescriptor.extractValueRange(solution, entity));
        }
        return doNullInValueRangeWrapping(new CompositeValueRange(childValueRangeList));
    }

    @Override
    public ValueRange<?> extractValueRange(Solution solution) {
        List<ValueRange<?>> childValueRangeList = new ArrayList<ValueRange<?>>(valueRangeDescriptorList.size());
        for (PlanningValueRangeDescriptor valueRangeDescriptor : valueRangeDescriptorList) {
            EntityIndependentPlanningValueRangeDescriptor entityIndependentValueRangeDescriptor
                    = (EntityIndependentPlanningValueRangeDescriptor) valueRangeDescriptor;
            childValueRangeList.add(entityIndependentValueRangeDescriptor.extractValueRange(solution));
        }
        return doNullInValueRangeWrapping(new CompositeValueRange(childValueRangeList));
    }

}
