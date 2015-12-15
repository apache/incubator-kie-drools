/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Non-cacheable.
 */
public class PillarSwapMove extends AbstractMove {

    protected final Collection<GenuineVariableDescriptor> variableDescriptors;

    protected final List<Object> leftPillar;
    protected final List<Object> rightPillar;

    public PillarSwapMove(Collection<GenuineVariableDescriptor> variableDescriptors,
            List<Object> leftPillar, List<Object> rightPillar) {
        this.variableDescriptors = variableDescriptors;
        this.leftPillar = leftPillar;
        this.rightPillar = rightPillar;
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

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        boolean movable = false;
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            Object leftValue = variableDescriptor.getValue(leftPillar.get(0));
            Object rightValue = variableDescriptor.getValue(rightPillar.get(0));
            if (!ObjectUtils.equals(leftValue, rightValue)) {
                movable = true;
                if (!variableDescriptor.isValueRangeEntityIndependent()) {
                    ValueRangeDescriptor valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
                    Solution workingSolution = scoreDirector.getWorkingSolution();
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

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new PillarSwapMove(variableDescriptors,
                rightPillar, leftPillar);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector scoreDirector) {
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            Object oldLeftValue = variableDescriptor.getValue(leftPillar.get(0));
            Object oldRightValue = variableDescriptor.getValue(rightPillar.get(0));
            if (!ObjectUtils.equals(oldLeftValue, oldRightValue)) {
                for (Object leftEntity : leftPillar) {
                    scoreDirector.beforeVariableChanged(variableDescriptor, leftEntity);
                    variableDescriptor.setValue(leftEntity, oldRightValue);
                    scoreDirector.afterVariableChanged(variableDescriptor, leftEntity);
                }
                for (Object rightEntity : rightPillar) {
                    scoreDirector.beforeVariableChanged(variableDescriptor, rightEntity);
                    variableDescriptor.setValue(rightEntity, oldLeftValue);
                    scoreDirector.afterVariableChanged(variableDescriptor, rightEntity);
                }
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
        List<Object> entities = new ArrayList<Object>(
                leftPillar.size() + rightPillar.size());
        entities.addAll(leftPillar);
        entities.addAll(rightPillar);
        return entities;
    }

    public Collection<? extends Object> getPlanningValues() {
        List<Object> values = new ArrayList<Object>(variableDescriptors.size() * 2);
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            values.add(variableDescriptor.getValue(leftPillar.get(0)));
            values.add(variableDescriptor.getValue(rightPillar.get(0)));
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
                    .append(leftPillar, other.leftPillar)
                    .append(rightPillar, other.rightPillar)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(variableDescriptors)
                .append(leftPillar)
                .append(rightPillar)
                .toHashCode();
    }

    public String toString() {
        StringBuilder s = new StringBuilder(variableDescriptors.size() * 16);
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
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            if (!first) {
                s.append(", ");
            }
            s.append(variableDescriptor.getValue(pillar.get(0)));
            first = false;
        }
    }

}
