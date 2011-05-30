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

package org.drools.planner.core.bruteforce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.core.domain.meta.PlanningEntityDescriptor;
import org.drools.planner.core.domain.meta.PlanningVariableDescriptor;
import org.drools.planner.core.solver.AbstractSolverScope;

public class BruteForcePlanningEntityIterator {

    private final Object planningEntity;

    private List<BruteForcePlanningVariableIterator> planningVariableIteratorList
            = new ArrayList<BruteForcePlanningVariableIterator>();

    public BruteForcePlanningEntityIterator(AbstractSolverScope solverScope, Object planningEntity) {
        this.planningEntity = planningEntity;
        PlanningEntityDescriptor planningEntityDescriptor = solverScope.getSolutionDescriptor()
                .getPlanningEntityDescriptor(planningEntity.getClass());
        for (PlanningVariableDescriptor planningVariableDescriptor
                : planningEntityDescriptor.getPlanningVariableDescriptors()) {
            BruteForcePlanningVariableIterator planningVariableIterator = new BruteForcePlanningVariableIterator(
                    solverScope, planningEntity, planningVariableDescriptor);
            planningVariableIteratorList.add(planningVariableIterator);
        }
    }

    public boolean hasNext() {
        for (BruteForcePlanningVariableIterator planningVariableIterator : planningVariableIteratorList) {
            if (planningVariableIterator.hasNext()) {
                return true;
            }
        }
        // All levels are maxed out
        return false;
    }

    public void next() {
        // Find the level to increment (for example in 115999)
        for (BruteForcePlanningVariableIterator planningVariableIterator : planningVariableIteratorList) {
            if (planningVariableIterator.hasNext()) {
                // Increment that level (for example 5 in 115999)
                planningVariableIterator.next();
                // Do not touch the higher levels (for example each 1 in 115999)
                break;
            } else {
                // Reset the lower levels (for example each 9 in 115999)
                planningVariableIterator.reset();
            }
        }
    }

    public void reset() {
        for (BruteForcePlanningVariableIterator planningVariableIterator : planningVariableIteratorList) {
            planningVariableIterator.reset();
        }
    }

    public Map<PlanningVariableDescriptor, Object> getVariableToValueMap() {
        Map variableToValueMap = new LinkedHashMap<PlanningVariableDescriptor, Object>();
        for (BruteForcePlanningVariableIterator planningVariableIterator : planningVariableIteratorList) {
            variableToValueMap.put(planningVariableIterator.getPlanningVariableDescriptor(),
                    planningVariableIterator.getWorkingValue());
        }
        return variableToValueMap;
    }
}
