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
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;

public class GenericChainedChangePartMove implements Move, TabuPropertyEnabled {

    private final List<Object> entitiesSubChain;
    private final Object firstEntity;
    private final Object lastEntity;
    private final PlanningVariableDescriptor planningVariableDescriptor;
    private final Object toPlanningValue;
    private final Object oldTrailingEntity;
    private final FactHandle oldTrailingEntityFactHandle;
    private final Object newTrailingEntity;
    private final FactHandle newTrailingEntityFactHandle;

    public GenericChainedChangePartMove(List<Object> entitiesSubChain,
            PlanningVariableDescriptor planningVariableDescriptor, Object toPlanningValue,
            Object oldTrailingEntity, FactHandle oldTrailingEntityFactHandle,
            Object newTrailingEntity, FactHandle newTrailingEntityFactHandle) {
        this.entitiesSubChain = entitiesSubChain;
        this.planningVariableDescriptor = planningVariableDescriptor;
        this.toPlanningValue = toPlanningValue;
        this.oldTrailingEntity = oldTrailingEntity;
        this.oldTrailingEntityFactHandle = oldTrailingEntityFactHandle;
        this.newTrailingEntity = newTrailingEntity;
        this.newTrailingEntityFactHandle = newTrailingEntityFactHandle;
        firstEntity = this.entitiesSubChain.get(0);
        lastEntity = this.entitiesSubChain.get(entitiesSubChain.size() - 1);
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return true; // Done by GenericChainedChangePartMoveFactory
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        Object oldFirstPlanningValue = planningVariableDescriptor.getValue(firstEntity);
        return new GenericChainedChangePartMove(entitiesSubChain,
                planningVariableDescriptor, oldFirstPlanningValue,
                newTrailingEntity, newTrailingEntityFactHandle, oldTrailingEntity, oldTrailingEntityFactHandle);
    }

    public void doMove(WorkingMemory workingMemory) {
        Object oldFirstPlanningValue = planningVariableDescriptor.getValue(firstEntity);
        // Close the old chain
        if (oldTrailingEntity != null) {
            planningVariableDescriptor.setValue(oldTrailingEntity, oldFirstPlanningValue);
            workingMemory.update(oldTrailingEntityFactHandle, oldTrailingEntity);
        }
        // Change the entity
        planningVariableDescriptor.setValue(firstEntity, toPlanningValue);
        for (Object entity : entitiesSubChain) {
            workingMemory.update(workingMemory.getFactHandle(entity), entity);
        }
        // Reroute the new chain
        if (newTrailingEntity != null) {
            planningVariableDescriptor.setValue(newTrailingEntity, lastEntity);
            workingMemory.update(newTrailingEntityFactHandle, newTrailingEntity);
        }
    }

    public Collection<? extends Object> getTabuProperties() {
        return entitiesSubChain;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof GenericChainedChangePartMove) {
            GenericChainedChangePartMove other = (GenericChainedChangePartMove) o;
            return new EqualsBuilder()
                    .append(entitiesSubChain, other.entitiesSubChain)
                    .append(planningVariableDescriptor.getVariablePropertyName(),
                            other.planningVariableDescriptor.getVariablePropertyName())
                    .append(toPlanningValue, other.toPlanningValue)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(entitiesSubChain)
                .append(planningVariableDescriptor.getVariablePropertyName())
                .append(toPlanningValue)
                .toHashCode();
    }

    public String toString() {
        return entitiesSubChain + " => " + toPlanningValue;
    }

}
