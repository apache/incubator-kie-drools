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

package org.optaplanner.core.impl.domain.valuerange.descriptor;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.composite.CompositeCountableValueRange;
import org.optaplanner.core.impl.domain.variable.descriptor.PlanningVariableDescriptor;
import org.optaplanner.core.impl.solution.Solution;

public class CompositePlanningValueRangeDescriptor extends AbstractPlanningValueRangeDescriptor
        implements EntityIndependentPlanningValueRangeDescriptor {

    protected final List<PlanningValueRangeDescriptor> childValueRangeDescriptorList;
    protected boolean entityIndependent;

    public CompositePlanningValueRangeDescriptor(
            PlanningVariableDescriptor variableDescriptor, boolean addNullInValueRange,
            List<PlanningValueRangeDescriptor> childValueRangeDescriptorList) {
        super(variableDescriptor, addNullInValueRange);
        this.childValueRangeDescriptorList = childValueRangeDescriptorList;
        entityIndependent = true;
        for (PlanningValueRangeDescriptor valueRangeDescriptor : childValueRangeDescriptorList) {
            if (!valueRangeDescriptor.isCountable()) {
                throw new IllegalStateException("The valueRangeDescriptor (" + this
                        + ") has a childValueRangeDescriptor (" + valueRangeDescriptor
                        + ") with countable (" + valueRangeDescriptor.isCountable() + ").");
            }
            if (!valueRangeDescriptor.isEntityIndependent()) {
                entityIndependent = false;
            }
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isEntityIndependent() {
        return entityIndependent;
    }

    @Override
    public boolean isValuesCacheable() {
        for (PlanningValueRangeDescriptor valueRangeDescriptor : childValueRangeDescriptorList) {
            if (!valueRangeDescriptor.isValuesCacheable()) {
                return false;
            }
        }
        return true;
    }

    public ValueRange<?> extractValueRange(Solution solution, Object entity) {
        List<CountableValueRange<?>> childValueRangeList = new ArrayList<CountableValueRange<?>>(childValueRangeDescriptorList.size());
        for (PlanningValueRangeDescriptor valueRangeDescriptor : childValueRangeDescriptorList) {
            childValueRangeList.add((CountableValueRange) valueRangeDescriptor.extractValueRange(solution, entity));
        }
        return doNullInValueRangeWrapping(new CompositeCountableValueRange(childValueRangeList));
    }

    @Override
    public ValueRange<?> extractValueRange(Solution solution) {
        List<CountableValueRange<?>> childValueRangeList = new ArrayList<CountableValueRange<?>>(childValueRangeDescriptorList.size());
        for (PlanningValueRangeDescriptor valueRangeDescriptor : childValueRangeDescriptorList) {
            EntityIndependentPlanningValueRangeDescriptor entityIndependentValueRangeDescriptor
                    = (EntityIndependentPlanningValueRangeDescriptor) valueRangeDescriptor;
            childValueRangeList.add((CountableValueRange) entityIndependentValueRangeDescriptor.extractValueRange(solution));
        }
        return doNullInValueRangeWrapping(new CompositeCountableValueRange(childValueRangeList));
    }

}
