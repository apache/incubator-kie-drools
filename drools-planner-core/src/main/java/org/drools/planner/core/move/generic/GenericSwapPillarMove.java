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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;

/**
 * Non-cacheable
 */
public class GenericSwapPillarMove implements Move, TabuPropertyEnabled {

    private final Collection<PlanningVariableDescriptor> planningVariableDescriptors;

    private final List<Object> leftPlanningEntityList;

    private final List<Object> rightPlanningEntityList;

    public GenericSwapPillarMove(Collection<PlanningVariableDescriptor> planningVariableDescriptors,
            List<Object> leftPlanningEntityList, List<Object> rightPlanningEntityList) {
        this.planningVariableDescriptors = planningVariableDescriptors;
        this.leftPlanningEntityList = leftPlanningEntityList;
        this.rightPlanningEntityList = rightPlanningEntityList;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptors) {
            Object leftValue = planningVariableDescriptor.getValue(leftPlanningEntityList.get(0));
            Object rightValue = planningVariableDescriptor.getValue(rightPlanningEntityList.get(0));
            if (!ObjectUtils.equals(leftValue, rightValue)) {
                return true;
            }
        }
        return false;
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new GenericSwapPillarMove(planningVariableDescriptors,
                rightPlanningEntityList, leftPlanningEntityList);
    }

    public void doMove(WorkingMemory workingMemory) {
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptors) {
            Object leftValue = planningVariableDescriptor.getValue(leftPlanningEntityList.get(0));
            Object rightValue = planningVariableDescriptor.getValue(rightPlanningEntityList.get(0));
            if (!ObjectUtils.equals(leftValue, rightValue)) {
                for (Object leftPlanningEntity : leftPlanningEntityList) {
                    planningVariableDescriptor.setValue(leftPlanningEntity, rightValue);
                    workingMemory.update(workingMemory.getFactHandle(leftPlanningEntity), leftPlanningEntity);
                }
                for (Object rightPlanningEntity : rightPlanningEntityList) {
                    planningVariableDescriptor.setValue(rightPlanningEntity, leftValue);
                    workingMemory.update(workingMemory.getFactHandle(rightPlanningEntity), rightPlanningEntity);
                }
            }
        }
    }

    public Collection<? extends Object> getTabuProperties() {
        List<Object> tabuProperties = new ArrayList<Object>(
                leftPlanningEntityList.size() + rightPlanningEntityList.size());
        tabuProperties.addAll(leftPlanningEntityList);
        tabuProperties.addAll(rightPlanningEntityList);
        return tabuProperties;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof GenericSwapPillarMove) {
            GenericSwapPillarMove other = (GenericSwapPillarMove) o;
            return new EqualsBuilder()
                    .append(leftPlanningEntityList, other.leftPlanningEntityList)
                    .append(rightPlanningEntityList, other.rightPlanningEntityList)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftPlanningEntityList)
                .append(rightPlanningEntityList)
                .toHashCode();
    }

    public String toString() {
        return leftPlanningEntityList + " <=> " + rightPlanningEntityList;
    }

}
