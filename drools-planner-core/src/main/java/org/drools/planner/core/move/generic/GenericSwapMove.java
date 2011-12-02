/*
 * Copyright 2011 JBoss Inc
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

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;

public class GenericSwapMove implements Move, TabuPropertyEnabled {

    private final Collection<PlanningVariableDescriptor> planningVariableDescriptors;

    private final Object leftPlanningEntity;
    private final FactHandle leftPlanningEntityFactHandle;

    private final Object rightPlanningEntity;
    private final FactHandle rightPlanningEntityFactHandle;

    public GenericSwapMove(Collection<PlanningVariableDescriptor> planningVariableDescriptors,
            Object leftPlanningEntity, FactHandle leftPlanningEntityFactHandle,
            Object rightPlanningEntity, FactHandle rightPlanningEntityFactHandle) {
        this.planningVariableDescriptors = planningVariableDescriptors;
        this.leftPlanningEntity = leftPlanningEntity;
        this.leftPlanningEntityFactHandle = leftPlanningEntityFactHandle;
        this.rightPlanningEntity = rightPlanningEntity;
        this.rightPlanningEntityFactHandle = rightPlanningEntityFactHandle;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptors) {
            Object leftValue = planningVariableDescriptor.getValue(leftPlanningEntity);
            Object rightValue = planningVariableDescriptor.getValue(rightPlanningEntity);
            if (!ObjectUtils.equals(leftValue, rightValue)) {
                return true;
            }
        }
        return false;
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new GenericSwapMove(planningVariableDescriptors,
                rightPlanningEntity, rightPlanningEntityFactHandle,
                leftPlanningEntity, leftPlanningEntityFactHandle);
    }

    public void doMove(WorkingMemory workingMemory) {
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptors) {
            Object leftValue = planningVariableDescriptor.getValue(leftPlanningEntity);
            Object rightValue = planningVariableDescriptor.getValue(rightPlanningEntity);
            if (!ObjectUtils.equals(leftValue, rightValue)) {
                planningVariableDescriptor.setValue(leftPlanningEntity, rightValue);
                workingMemory.update(leftPlanningEntityFactHandle, leftPlanningEntity);
                planningVariableDescriptor.setValue(rightPlanningEntity, leftValue);
                workingMemory.update(rightPlanningEntityFactHandle, rightPlanningEntity);
            }
        }
    }

    public Collection<? extends Object> getTabuProperties() {
        return Arrays.<Object>asList(leftPlanningEntity, rightPlanningEntity);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof GenericSwapMove) {
            GenericSwapMove other = (GenericSwapMove) o;
            return new EqualsBuilder()
                    .append(leftPlanningEntity, other.leftPlanningEntity)
                    .append(rightPlanningEntity, other.rightPlanningEntity)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftPlanningEntity)
                .append(rightPlanningEntity)
                .toHashCode();
    }

    public String toString() {
        return leftPlanningEntity + " <=> " + rightPlanningEntity;
    }

}
