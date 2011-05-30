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

import java.util.Collection;
import java.util.Iterator;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.meta.PlanningVariableDescriptor;
import org.drools.planner.core.solver.AbstractSolverScope;

public class BruteForcePlanningVariableIterator {

    private final AbstractSolverScope solverScope;
    private final Object planningEntity;
    private final FactHandle planningEntityFactHandle;
    private final PlanningVariableDescriptor planningVariableDescriptor;

    private Collection<?> planningValues;
    private Iterator<?> planningValueIterator;

    private Object workingValue;

    public BruteForcePlanningVariableIterator(AbstractSolverScope solverScope, Object planningEntity,
            FactHandle planningEntityFactHandle, PlanningVariableDescriptor planningVariableDescriptor) {
        this.solverScope = solverScope;
        this.planningEntity = planningEntity;
        this.planningEntityFactHandle = planningEntityFactHandle;
        this.planningVariableDescriptor = planningVariableDescriptor;

        planningValues = planningVariableDescriptor.getRangeValues(solverScope.getWorkingSolution());
        planningValueIterator = planningValues.iterator();
    }

    public PlanningVariableDescriptor getPlanningVariableDescriptor() {
        return planningVariableDescriptor;
    }

    public Object getWorkingValue() {
        return workingValue;
    }

    public boolean hasNext() {
        return planningValueIterator.hasNext();
    }

    public void next() {
        Object value = planningValueIterator.next();
        changeWorkingValue(value);
    }

    public void reset() {
        planningValueIterator = planningValues.iterator();
        Object value = planningValueIterator.next();
        changeWorkingValue(value);
    }

    private void changeWorkingValue(Object value) {
        WorkingMemory workingMemory = solverScope.getWorkingMemory();
        planningVariableDescriptor.setValue(planningEntity, value);
        workingMemory.update(planningEntityFactHandle, planningEntity);
        workingValue = value;
    }

}
