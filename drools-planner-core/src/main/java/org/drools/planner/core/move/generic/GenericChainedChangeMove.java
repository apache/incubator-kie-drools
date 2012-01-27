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

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.move.Move;

public class GenericChainedChangeMove extends GenericChangeMove {

    private final Object oldChainedEntity;
    private final FactHandle oldChainedEntityFactHandle;
    private final Object newChainedEntity;
    private final FactHandle newChainedEntityFactHandle;

    public GenericChainedChangeMove(Object planningEntity, FactHandle planningEntityFactHandle,
            PlanningVariableDescriptor planningVariableDescriptor, Object toPlanningValue,
            Object oldChainedEntity, FactHandle oldChainedEntityFactHandle,
            Object newChainedEntity, FactHandle newChainedEntityFactHandle) {
        super(planningEntity, planningEntityFactHandle, planningVariableDescriptor, toPlanningValue);
        this.oldChainedEntity = oldChainedEntity;
        this.oldChainedEntityFactHandle = oldChainedEntityFactHandle;
        this.newChainedEntity = newChainedEntity;
        this.newChainedEntityFactHandle = newChainedEntityFactHandle;
    }

    @Override
    public Move createUndoMove(WorkingMemory workingMemory) {
        Object oldPlanningValue = planningVariableDescriptor.getValue(planningEntity);
        return new GenericChainedChangeMove(planningEntity, planningEntityFactHandle,
                planningVariableDescriptor, oldPlanningValue,
                newChainedEntity, newChainedEntityFactHandle, oldChainedEntity, oldChainedEntityFactHandle);
    }

    @Override
    public void doMove(WorkingMemory workingMemory) {
        Object oldPlanningValue = planningVariableDescriptor.getValue(planningEntity);
        // Change the entity
        planningVariableDescriptor.setValue(planningEntity, toPlanningValue);
        workingMemory.update(planningEntityFactHandle, planningEntity);
        // Close the old chain
        if (oldPlanningValue != null) {
            planningVariableDescriptor.setValue(oldChainedEntity, oldPlanningValue);
            workingMemory.update(oldChainedEntityFactHandle, oldChainedEntity);
        }
        // Reroute the new chain
        if (newChainedEntity != null) {
            planningVariableDescriptor.setValue(newChainedEntity, toPlanningValue);
            workingMemory.update(newChainedEntityFactHandle, newChainedEntity);
        }
    }

}
