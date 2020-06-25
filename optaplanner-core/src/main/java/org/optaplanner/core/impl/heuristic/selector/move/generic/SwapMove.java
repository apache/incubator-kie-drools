/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.Objects;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class SwapMove<Solution_> extends AbstractMove<Solution_> {

    protected final List<GenuineVariableDescriptor<Solution_>> variableDescriptorList;

    protected final Object leftEntity;
    protected final Object rightEntity;

    public SwapMove(List<GenuineVariableDescriptor<Solution_>> variableDescriptorList, Object leftEntity, Object rightEntity) {
        this.variableDescriptorList = variableDescriptorList;
        this.leftEntity = leftEntity;
        this.rightEntity = rightEntity;
    }

    public List<String> getVariableNameList() {
        List<String> variableNameList = new ArrayList<>(variableDescriptorList.size());
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            variableNameList.add(variableDescriptor.getVariableName());
        }
        return variableNameList;
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

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        boolean movable = false;
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            Object leftValue = variableDescriptor.getValue(leftEntity);
            Object rightValue = variableDescriptor.getValue(rightEntity);
            if (!Objects.equals(leftValue, rightValue)) {
                movable = true;
                if (!variableDescriptor.isValueRangeEntityIndependent()) {
                    ValueRangeDescriptor<Solution_> valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
                    Solution_ workingSolution = scoreDirector.getWorkingSolution();
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

    @Override
    public SwapMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        return new SwapMove<>(variableDescriptorList, rightEntity, leftEntity);
    }

    @Override
    public SwapMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new SwapMove<>(variableDescriptorList,
                destinationScoreDirector.lookUpWorkingObject(leftEntity),
                destinationScoreDirector.lookUpWorkingObject(rightEntity));
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        InnerScoreDirector<Solution_> innerScoreDirector = (InnerScoreDirector<Solution_>) scoreDirector;
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            Object oldLeftValue = variableDescriptor.getValue(leftEntity);
            Object oldRightValue = variableDescriptor.getValue(rightEntity);
            if (!Objects.equals(oldLeftValue, oldRightValue)) {
                innerScoreDirector.beforeVariableChanged(variableDescriptor, leftEntity);
                variableDescriptor.setValue(leftEntity, oldRightValue);
                innerScoreDirector.afterVariableChanged(variableDescriptor, leftEntity);
                innerScoreDirector.beforeVariableChanged(variableDescriptor, rightEntity);
                variableDescriptor.setValue(rightEntity, oldLeftValue);
                innerScoreDirector.afterVariableChanged(variableDescriptor, rightEntity);
            }
        }
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public String getSimpleMoveTypeDescription() {
        StringBuilder moveTypeDescription = new StringBuilder(20 * (variableDescriptorList.size() + 1));
        moveTypeDescription.append(getClass().getSimpleName()).append("(");
        String delimiter = "";
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            moveTypeDescription.append(delimiter).append(variableDescriptor.getSimpleEntityAndVariableName());
            delimiter = ", ";
        }
        moveTypeDescription.append(")");
        return moveTypeDescription.toString();
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftEntity, rightEntity);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        List<Object> values = new ArrayList<>(variableDescriptorList.size() * 2);
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            values.add(variableDescriptor.getValue(leftEntity));
            values.add(variableDescriptor.getValue(rightEntity));
        }
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SwapMove<?> swapMove = (SwapMove<?>) o;
        return Objects.equals(variableDescriptorList, swapMove.variableDescriptorList) &&
                Objects.equals(leftEntity, swapMove.leftEntity) &&
                Objects.equals(rightEntity, swapMove.rightEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableDescriptorList, leftEntity, rightEntity);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(variableDescriptorList.size() * 16);
        s.append(leftEntity).append(" {");
        appendVariablesToString(s, leftEntity);
        s.append("} <-> ");
        s.append(rightEntity).append(" {");
        appendVariablesToString(s, rightEntity);
        s.append("}");
        return s.toString();
    }

    protected void appendVariablesToString(StringBuilder s, Object entity) {
        boolean first = true;
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            if (!first) {
                s.append(", ");
            }
            s.append(variableDescriptor.getValue(entity));
            first = false;
        }
    }

}
