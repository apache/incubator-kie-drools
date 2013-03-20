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
import org.optaplanner.core.api.domain.value.ValueRangeType;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.solution.Solution;

public class CompositePlanningValueRangeDescriptor extends AbstractPlanningValueRangeDescriptor {

    protected final List<PlanningValueRangeDescriptor> valueRangeDescriptorList;
    protected boolean entityDepentent;

    public CompositePlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor,
            List<PlanningValueRangeDescriptor> valueRangeDescriptorList) {
        super(variableDescriptor);
        this.valueRangeDescriptorList = valueRangeDescriptorList;
        entityDepentent = false;
        for (PlanningValueRangeDescriptor valueRangeDescriptor : valueRangeDescriptorList) {
            if (valueRangeDescriptor instanceof UndefinedPlanningValueRangeDescriptor) {
                throw new IllegalArgumentException("The planningEntityClass ("
                        + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                        + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                        + ") with multiple " + ValueRange.class.getSimpleName() + " annotations,"
                        + " including one of type (" + ValueRangeType.UNDEFINED + ").");
            } else if (valueRangeDescriptor instanceof FromEntityPropertyPlanningValueRangeDescriptor) {
                entityDepentent = true;
            } else if (!(valueRangeDescriptor instanceof FromSolutionPropertyPlanningValueRangeDescriptor)) {
                throw new IllegalStateException("The valueRangeDescriptorClass ("
                        + valueRangeDescriptor.getClass() + ") is not implemented.");
            }
        }
    }

    public boolean isEntityDependent() {
        return entityDepentent;
    }

    public Collection<?> extractAllValuesWithFiltering(Solution solution) {
        Collection<Object> values = new ArrayList<Object>(0);
        for (PlanningValueRangeDescriptor valueRangeDescriptor : valueRangeDescriptorList) {
            values.addAll(valueRangeDescriptor.extractAllValuesWithFiltering(solution));
        }
        return values;
    }

    public Collection<?> extractValuesWithFiltering(Solution solution, Object planningEntity) {
        Collection<Object> values = new ArrayList<Object>(0);
        for (PlanningValueRangeDescriptor valueRangeDescriptor : valueRangeDescriptorList) {
            values.addAll(valueRangeDescriptor.extractValuesWithFiltering(solution, planningEntity));
        }
        return values;
    }

    public long getProblemScale(Solution solution, Object planningEntity) {
        long problemScale = 0L;
        for (PlanningValueRangeDescriptor valueRangeDescriptor : valueRangeDescriptorList) {
            problemScale += valueRangeDescriptor.getProblemScale(solution, planningEntity);
        }
        return problemScale;
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

}
