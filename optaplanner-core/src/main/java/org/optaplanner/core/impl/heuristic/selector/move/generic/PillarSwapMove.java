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
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * This {@link Move} is not cacheable.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class PillarSwapMove<Solution_> extends AbstractMove<Solution_> {

    protected final List<GenuineVariableDescriptor<Solution_>> variableDescriptorList;

    protected final List<Object> leftPillar;
    protected final List<Object> rightPillar;

    public PillarSwapMove(List<GenuineVariableDescriptor<Solution_>> variableDescriptorList,
            List<Object> leftPillar, List<Object> rightPillar) {
        this.variableDescriptorList = variableDescriptorList;
        this.leftPillar = leftPillar;
        this.rightPillar = rightPillar;
    }

    public List<String> getVariableNameList() {
        List<String> variableNameList = new ArrayList<>(variableDescriptorList.size());
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            variableNameList.add(variableDescriptor.getVariableName());
        }
        return variableNameList;
    }

    public List<Object> getLeftPillar() {
        return leftPillar;
    }

    public List<Object> getRightPillar() {
        return rightPillar;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        boolean movable = false;
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            Object leftValue = variableDescriptor.getValue(leftPillar.get(0));
            Object rightValue = variableDescriptor.getValue(rightPillar.get(0));
            if (!Objects.equals(leftValue, rightValue)) {
                movable = true;
                if (!variableDescriptor.isValueRangeEntityIndependent()) {
                    ValueRangeDescriptor<Solution_> valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
                    Solution_ workingSolution = scoreDirector.getWorkingSolution();
                    for (Object rightEntity : rightPillar) {
                        ValueRange rightValueRange = valueRangeDescriptor.extractValueRange(workingSolution, rightEntity);
                        if (!rightValueRange.contains(leftValue)) {
                            return false;
                        }
                    }
                    for (Object leftEntity : leftPillar) {
                        ValueRange leftValueRange = valueRangeDescriptor.extractValueRange(workingSolution, leftEntity);
                        if (!leftValueRange.contains(rightValue)) {
                            return false;
                        }
                    }
                }
            }
        }
        return movable;
    }

    @Override
    public PillarSwapMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        return new PillarSwapMove<>(variableDescriptorList, rightPillar, leftPillar);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        InnerScoreDirector<Solution_> innerScoreDirector = (InnerScoreDirector<Solution_>) scoreDirector;
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            Object oldLeftValue = variableDescriptor.getValue(leftPillar.get(0));
            Object oldRightValue = variableDescriptor.getValue(rightPillar.get(0));
            if (!Objects.equals(oldLeftValue, oldRightValue)) {
                for (Object leftEntity : leftPillar) {
                    innerScoreDirector.beforeVariableChanged(variableDescriptor, leftEntity);
                    variableDescriptor.setValue(leftEntity, oldRightValue);
                    innerScoreDirector.afterVariableChanged(variableDescriptor, leftEntity);
                }
                for (Object rightEntity : rightPillar) {
                    innerScoreDirector.beforeVariableChanged(variableDescriptor, rightEntity);
                    variableDescriptor.setValue(rightEntity, oldLeftValue);
                    innerScoreDirector.afterVariableChanged(variableDescriptor, rightEntity);
                }
            }
        }
    }

    @Override
    public PillarSwapMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new PillarSwapMove<>(variableDescriptorList,
                rebaseList(leftPillar, destinationScoreDirector), rebaseList(rightPillar, destinationScoreDirector));
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
        List<Object> entities = new ArrayList<>(
                leftPillar.size() + rightPillar.size());
        entities.addAll(leftPillar);
        entities.addAll(rightPillar);
        return entities;
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        List<Object> values = new ArrayList<>(variableDescriptorList.size() * 2);
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            values.add(variableDescriptor.getValue(leftPillar.get(0)));
            values.add(variableDescriptor.getValue(rightPillar.get(0)));
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
        final PillarSwapMove<?> other = (PillarSwapMove<?>) o;
        return Objects.equals(variableDescriptorList, other.variableDescriptorList) &&
                Objects.equals(leftPillar, other.leftPillar) &&
                Objects.equals(rightPillar, other.rightPillar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableDescriptorList, leftPillar, rightPillar);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(variableDescriptorList.size() * 16);
        s.append(leftPillar).append(" {");
        appendVariablesToString(s, leftPillar);
        s.append("} <-> ");
        s.append(rightPillar).append(" {");
        appendVariablesToString(s, rightPillar);
        s.append("}");
        return s.toString();
    }

    protected void appendVariablesToString(StringBuilder s, List<Object> pillar) {
        boolean first = true;
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            if (!first) {
                s.append(", ");
            }
            s.append(variableDescriptor.getValue(pillar.get(0)));
            first = false;
        }
    }

}
