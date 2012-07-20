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
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;

/**
 * Non-cacheable
 */
public class GenericSwapPillarMove implements Move {

    private final Collection<PlanningVariableDescriptor> planningVariableDescriptors;

    private final List<Object> leftPlanningEntityList;
    private final List<Object> rightPlanningEntityList;

    public GenericSwapPillarMove(Collection<PlanningVariableDescriptor> planningVariableDescriptors,
            List<Object> leftPlanningEntityList, List<Object> rightPlanningEntityList) {
        this.planningVariableDescriptors = planningVariableDescriptors;
        this.leftPlanningEntityList = leftPlanningEntityList;
        this.rightPlanningEntityList = rightPlanningEntityList;
    }

    public List<Object> getLeftPlanningEntityList() {
        return leftPlanningEntityList;
    }

    public List<Object> getRightPlanningEntityList() {
        return rightPlanningEntityList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        for (PlanningVariableDescriptor variableDescriptor : planningVariableDescriptors) {
            Object leftValue = variableDescriptor.getValue(leftPlanningEntityList.get(0));
            Object rightValue = variableDescriptor.getValue(rightPlanningEntityList.get(0));
            if (!ObjectUtils.equals(leftValue, rightValue)) {
                return true;
            }
        }
        return false;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new GenericSwapPillarMove(planningVariableDescriptors,
                rightPlanningEntityList, leftPlanningEntityList);
    }

    public void doMove(ScoreDirector scoreDirector) {
        for (PlanningVariableDescriptor variableDescriptor : planningVariableDescriptors) {
            Object oldLeftValue = variableDescriptor.getValue(leftPlanningEntityList.get(0));
            Object oldRightValue = variableDescriptor.getValue(rightPlanningEntityList.get(0));
            if (!ObjectUtils.equals(oldLeftValue, oldRightValue)) {
                for (Object leftPlanningEntity : leftPlanningEntityList) {
                    scoreDirector.beforeVariableChanged(leftPlanningEntity, variableDescriptor.getVariableName());
                    variableDescriptor.setValue(leftPlanningEntity, oldRightValue);
                    scoreDirector.afterVariableChanged(leftPlanningEntity, variableDescriptor.getVariableName());
                }
                for (Object rightPlanningEntity : rightPlanningEntityList) {
                    scoreDirector.beforeVariableChanged(rightPlanningEntity, variableDescriptor.getVariableName());
                    variableDescriptor.setValue(rightPlanningEntity, oldLeftValue);
                    scoreDirector.afterVariableChanged(rightPlanningEntity, variableDescriptor.getVariableName());
                }
            }
        }
    }

    public Collection<? extends Object> getPlanningEntities() {
        List<Object> entities = new ArrayList<Object>(
                leftPlanningEntityList.size() + rightPlanningEntityList.size());
        entities.addAll(leftPlanningEntityList);
        entities.addAll(rightPlanningEntityList);
        return entities;
    }

    public Collection<? extends Object> getPlanningValues() {
        List<Object> values = new ArrayList<Object>(planningVariableDescriptors.size() * 2);
        for (PlanningVariableDescriptor variableDescriptor : planningVariableDescriptors) {
            values.add(variableDescriptor.getValue(leftPlanningEntityList.get(0)));
            values.add(variableDescriptor.getValue(rightPlanningEntityList.get(0)));
        }
        return values;
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
