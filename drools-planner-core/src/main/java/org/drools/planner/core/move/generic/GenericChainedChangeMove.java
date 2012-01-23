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

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;

public class GenericChainedChangeMove extends GenericChangeMove {

    private final Object chainedEntity;
    private final FactHandle chainedEntityFactHandle;

    public GenericChainedChangeMove(Object planningEntity, FactHandle planningEntityFactHandle,
            PlanningVariableDescriptor planningVariableDescriptor, Object toPlanningValue,
            Object chainedEntity, FactHandle chainedEntityFactHandle) {
        super(planningEntity, planningEntityFactHandle, planningVariableDescriptor, toPlanningValue);
        this.chainedEntity = chainedEntity;
        this.chainedEntityFactHandle = chainedEntityFactHandle;
    }

    @Override
    public Move createUndoMove(WorkingMemory workingMemory) {
        Object oldPlanningValue = planningVariableDescriptor.getValue(planningEntity);
        return new GenericChainedChangeMove(planningEntity, planningEntityFactHandle,
                planningVariableDescriptor, oldPlanningValue, chainedEntity, chainedEntityFactHandle);
    }

    @Override
    public void doMove(WorkingMemory workingMemory) {
        Object oldPlanningValue = planningVariableDescriptor.getValue(planningEntity);
        planningVariableDescriptor.setValue(planningEntity, toPlanningValue);
        workingMemory.update(planningEntityFactHandle, planningEntity);
        planningVariableDescriptor.setValue(chainedEntity, oldPlanningValue);
        workingMemory.update(chainedEntityFactHandle, chainedEntity);
    }

}
