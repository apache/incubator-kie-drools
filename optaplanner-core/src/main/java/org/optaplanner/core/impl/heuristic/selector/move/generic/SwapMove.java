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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class SwapMove extends AbstractMove {

    protected final Collection<GenuineVariableDescriptor> variableDescriptors;

    protected final Object leftEntity;
    protected final Object rightEntity;

    public SwapMove(Collection<GenuineVariableDescriptor> variableDescriptors, Object leftEntity, Object rightEntity) {
        this.variableDescriptors = variableDescriptors;
        this.leftEntity = leftEntity;
        this.rightEntity = rightEntity;
    }

    public Object getLeftEntity() {
        return leftEntity;
    }

    public Object getRightEntity() {
        return rightEntity;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        boolean movable = false;
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            Object leftValue = variableDescriptor.getValue(leftEntity);
            Object rightValue = variableDescriptor.getValue(rightEntity);
            if (!ObjectUtils.equals(leftValue, rightValue)) {
                movable = true;
                if (!variableDescriptor.isValueRangeEntityIndependent()) {
                    ValueRangeDescriptor valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
                    Solution workingSolution = scoreDirector.getWorkingSolution();
                    ValueRange rightValueRange = valueRangeDescriptor.extractValueRange(workingSolution, rightEntity);
                    if (!rightValueRange.contains(leftValue)) {
                        return false;
                    }
                    ValueRange leftValueRange = valueRangeDescriptor.extractValueRange(workingSolution, leftEntity);
                    if (!leftValueRange.contains(rightValue)) {
                        return false;
                    }
                }
            }
        }
        return movable;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new SwapMove(variableDescriptors, rightEntity, leftEntity);
    }

    public void doMove(ScoreDirector scoreDirector) {
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            Object oldLeftValue = variableDescriptor.getValue(leftEntity);
            Object oldRightValue = variableDescriptor.getValue(rightEntity);
            if (!ObjectUtils.equals(oldLeftValue, oldRightValue)) {
                scoreDirector.beforeVariableChanged(variableDescriptor, leftEntity);
                variableDescriptor.setValue(leftEntity, oldRightValue);
                scoreDirector.afterVariableChanged(variableDescriptor, leftEntity);
                scoreDirector.beforeVariableChanged(variableDescriptor, rightEntity);
                variableDescriptor.setValue(rightEntity, oldLeftValue);
                scoreDirector.afterVariableChanged(variableDescriptor, rightEntity);
            }
        }
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public String getSimpleMoveTypeDescription() {
        StringBuilder moveTypeDescription = new StringBuilder(20 * (variableDescriptors.size() + 1));
        moveTypeDescription.append(getClass().getSimpleName()).append("(");
        String delimiter = "";
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            moveTypeDescription.append(delimiter).append(variableDescriptor.getSimpleEntityAndVariableName());
            delimiter = ", ";
        }
        moveTypeDescription.append(")");
        return moveTypeDescription.toString();
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftEntity, rightEntity);
    }

    public Collection<? extends Object> getPlanningValues() {
        List<Object> values = new ArrayList<Object>(variableDescriptors.size() * 2);
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            values.add(variableDescriptor.getValue(leftEntity));
            values.add(variableDescriptor.getValue(rightEntity));
        }
        return values;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof SwapMove) {
            SwapMove other = (SwapMove) o;
            return new EqualsBuilder()
                    .append(leftEntity, other.leftEntity)
                    .append(rightEntity, other.rightEntity)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftEntity)
                .append(rightEntity)
                .toHashCode();
    }

    public String toString() {
        return leftEntity + " <=> " + rightEntity;
    }

}
