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

package org.drools.planner.core.domain.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.solution.Solution;

public class CompositePlanningValueRangeDescriptor extends AbstractPlanningValueRangeDescriptor {

    private final List<PlanningValueRangeDescriptor> valueRangeDescriptorList;

    public CompositePlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor,
            List<PlanningValueRangeDescriptor> valueRangeDescriptorList) {
        super(variableDescriptor);
        this.valueRangeDescriptorList = valueRangeDescriptorList;
    }

    public Collection<?> extractAllValues(Solution solution) {
        Collection<Object> values = new ArrayList<Object>(0);
        for (PlanningValueRangeDescriptor valueRangeDescriptor : valueRangeDescriptorList) {
            values.addAll(valueRangeDescriptor.extractAllValues(solution));
        }
        return values;
    }

    public Collection<?> extractValues(Solution solution, Object planningEntity) {
        Collection<Object> values = new ArrayList<Object>(0);
        for (PlanningValueRangeDescriptor valueRangeDescriptor : valueRangeDescriptorList) {
            values.addAll(valueRangeDescriptor.extractValues(solution, planningEntity));
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
