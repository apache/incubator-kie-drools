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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Non-cacheable
 */
public class PillarSwapMove implements Move {

    private final Collection<PlanningVariableDescriptor> variableDescriptors;

    private final List<Object> leftEntityList;
    private final List<Object> rightEntityList;

    public PillarSwapMove(Collection<PlanningVariableDescriptor> variableDescriptors,
            List<Object> leftEntityList, List<Object> rightEntityList) {
        this.variableDescriptors = variableDescriptors;
        this.leftEntityList = leftEntityList;
        this.rightEntityList = rightEntityList;
    }

    public List<Object> getLeftEntityList() {
        return leftEntityList;
    }

    public List<Object> getRightEntityList() {
        return rightEntityList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            Object leftValue = variableDescriptor.getValue(leftEntityList.get(0));
            Object rightValue = variableDescriptor.getValue(rightEntityList.get(0));
            if (!ObjectUtils.equals(leftValue, rightValue)) {
                return true;
            }
        }
        return false;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new PillarSwapMove(variableDescriptors,
                rightEntityList, leftEntityList);
    }

    public void doMove(ScoreDirector scoreDirector) {
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            Object oldLeftValue = variableDescriptor.getValue(leftEntityList.get(0));
            Object oldRightValue = variableDescriptor.getValue(rightEntityList.get(0));
            if (!ObjectUtils.equals(oldLeftValue, oldRightValue)) {
                for (Object leftPlanningEntity : leftEntityList) {
                    scoreDirector.beforeVariableChanged(leftPlanningEntity, variableDescriptor.getVariableName());
                    variableDescriptor.setValue(leftPlanningEntity, oldRightValue);
                    scoreDirector.afterVariableChanged(leftPlanningEntity, variableDescriptor.getVariableName());
                }
                for (Object rightPlanningEntity : rightEntityList) {
                    scoreDirector.beforeVariableChanged(rightPlanningEntity, variableDescriptor.getVariableName());
                    variableDescriptor.setValue(rightPlanningEntity, oldLeftValue);
                    scoreDirector.afterVariableChanged(rightPlanningEntity, variableDescriptor.getVariableName());
                }
            }
        }
    }

    public Collection<? extends Object> getPlanningEntities() {
        List<Object> entities = new ArrayList<Object>(
                leftEntityList.size() + rightEntityList.size());
        entities.addAll(leftEntityList);
        entities.addAll(rightEntityList);
        return entities;
    }

    public Collection<? extends Object> getPlanningValues() {
        List<Object> values = new ArrayList<Object>(variableDescriptors.size() * 2);
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            values.add(variableDescriptor.getValue(leftEntityList.get(0)));
            values.add(variableDescriptor.getValue(rightEntityList.get(0)));
        }
        return values;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof PillarSwapMove) {
            PillarSwapMove other = (PillarSwapMove) o;
            return new EqualsBuilder()
                    .append(variableDescriptors, other.variableDescriptors)
                    .append(leftEntityList, other.leftEntityList)
                    .append(rightEntityList, other.rightEntityList)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(variableDescriptors)
                .append(leftEntityList)
                .append(rightEntityList)
                .toHashCode();
    }

    public String toString() {
        return leftEntityList + " <=> " + rightEntityList;
    }

}
