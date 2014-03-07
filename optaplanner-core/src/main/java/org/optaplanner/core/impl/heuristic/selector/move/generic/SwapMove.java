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
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class SwapMove implements Move {

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
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            Object leftValue = variableDescriptor.getValue(leftEntity);
            Object rightValue = variableDescriptor.getValue(rightEntity);
            if (!ObjectUtils.equals(leftValue, rightValue)) {
                return true;
            }
        }
        return false;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new SwapMove(variableDescriptors, rightEntity, leftEntity);
    }

    public void doMove(ScoreDirector scoreDirector) {
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            Object oldLeftValue = variableDescriptor.getValue(leftEntity);
            Object oldRightValue = variableDescriptor.getValue(rightEntity);
            if (!ObjectUtils.equals(oldLeftValue, oldRightValue)) {
                scoreDirector.beforeVariableChanged(leftEntity, variableDescriptor.getVariableName());
                variableDescriptor.setValue(leftEntity, oldRightValue);
                scoreDirector.afterVariableChanged(leftEntity, variableDescriptor.getVariableName());
                scoreDirector.beforeVariableChanged(rightEntity, variableDescriptor.getVariableName());
                variableDescriptor.setValue(rightEntity, oldLeftValue);
                scoreDirector.afterVariableChanged(rightEntity, variableDescriptor.getVariableName());
            }
        }
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
