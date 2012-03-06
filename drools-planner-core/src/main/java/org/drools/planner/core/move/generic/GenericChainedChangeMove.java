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

package org.drools.planner.core.move.generic;

import org.apache.commons.lang.ObjectUtils;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.move.Move;

public class GenericChainedChangeMove extends GenericChangeMove {

    private final Object oldTrailingEntity;
    private final FactHandle oldTrailingEntityFactHandle;
    private final Object newTrailingEntity;
    private final FactHandle newTrailingEntityFactHandle;

    public GenericChainedChangeMove(Object planningEntity, FactHandle planningEntityFactHandle,
            PlanningVariableDescriptor planningVariableDescriptor, Object toPlanningValue,
            Object oldTrailingEntity, FactHandle oldTrailingEntityFactHandle,
            Object newTrailingEntity, FactHandle newTrailingEntityFactHandle) {
        super(planningEntity, planningEntityFactHandle, planningVariableDescriptor, toPlanningValue);
        this.oldTrailingEntity = oldTrailingEntity;
        this.oldTrailingEntityFactHandle = oldTrailingEntityFactHandle;
        this.newTrailingEntity = newTrailingEntity;
        this.newTrailingEntityFactHandle = newTrailingEntityFactHandle;
    }

    @Override
    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return super.isMoveDoable(workingMemory) && !ObjectUtils.equals(planningEntity, toPlanningValue);
    }

    @Override
    public Move createUndoMove(WorkingMemory workingMemory) {
        Object oldPlanningValue = planningVariableDescriptor.getValue(planningEntity);
        return new GenericChainedChangeMove(planningEntity, planningEntityFactHandle,
                planningVariableDescriptor, oldPlanningValue,
                newTrailingEntity, newTrailingEntityFactHandle, oldTrailingEntity, oldTrailingEntityFactHandle);
    }

    @Override
    public void doMove(WorkingMemory workingMemory) {
        Object oldPlanningValue = planningVariableDescriptor.getValue(planningEntity);

        // Close the old chain
        if (oldTrailingEntity != null) {
            planningVariableDescriptor.setValue(oldTrailingEntity, oldPlanningValue);
            workingMemory.update(oldTrailingEntityFactHandle, oldTrailingEntity);
        }
        // Change the entity
        planningVariableDescriptor.setValue(planningEntity, toPlanningValue);
        workingMemory.update(planningEntityFactHandle, planningEntity);
        // Reroute the new chain
        if (newTrailingEntity != null) {
            planningVariableDescriptor.setValue(newTrailingEntity, planningEntity);
            workingMemory.update(newTrailingEntityFactHandle, newTrailingEntity);
        }
    }

}
