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

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.move.Move;

public class GenericChangeMove implements Move {

    protected final Object planningEntity;
    protected final PlanningVariableDescriptor planningVariableDescriptor;
    protected final Object toPlanningValue;

    public GenericChangeMove(Object planningEntity, PlanningVariableDescriptor planningVariableDescriptor,
            Object toPlanningValue) {
        this.planningEntity = planningEntity;
        this.planningVariableDescriptor = planningVariableDescriptor;
        this.toPlanningValue = toPlanningValue;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        Object oldPlanningValue = planningVariableDescriptor.getValue(planningEntity);
        return !ObjectUtils.equals(oldPlanningValue, toPlanningValue);
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        Object oldPlanningValue = planningVariableDescriptor.getValue(planningEntity);
        return new GenericChangeMove(planningEntity, planningVariableDescriptor, oldPlanningValue);
    }

    public void doMove(WorkingMemory workingMemory) {
        planningVariableDescriptor.setValue(planningEntity, toPlanningValue);
        workingMemory.update(workingMemory.getFactHandle(planningEntity), planningEntity);
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(planningEntity);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toPlanningValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof GenericChangeMove) {
            GenericChangeMove other = (GenericChangeMove) o;
            return new EqualsBuilder()
                    .append(planningEntity, other.planningEntity)
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
                .append(planningEntity)
                .append(planningVariableDescriptor.getVariablePropertyName())
                .append(toPlanningValue)
                .toHashCode();
    }

    public String toString() {
        return planningEntity + " => " + toPlanningValue;
    }

}
