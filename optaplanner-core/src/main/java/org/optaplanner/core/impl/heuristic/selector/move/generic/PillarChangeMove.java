/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;
import java.util.Collections;
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
public class PillarChangeMove extends AbstractMove {

    protected final GenuineVariableDescriptor variableDescriptor;

    protected final List<Object> pillar;
    protected final Object toPlanningValue;

    public PillarChangeMove(List<Object> pillar, GenuineVariableDescriptor variableDescriptor, Object toPlanningValue) {
        this.pillar = pillar;
        this.variableDescriptor = variableDescriptor;
        this.toPlanningValue = toPlanningValue;
    }

    public List<Object> getPillar() {
        return pillar;
    }

    public Object getToPlanningValue() {
        return toPlanningValue;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        Object oldValue = variableDescriptor.getValue(pillar.get(0));
        if (ObjectUtils.equals(oldValue, toPlanningValue)) {
            return false;
        }
        if (!variableDescriptor.isValueRangeEntityIndependent()) {
            ValueRangeDescriptor valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
            Solution workingSolution = scoreDirector.getWorkingSolution();
            for (Object entity : pillar) {
                ValueRange rightValueRange = valueRangeDescriptor.extractValueRange(workingSolution, entity);
                if (!rightValueRange.contains(toPlanningValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        Object oldValue = variableDescriptor.getValue(pillar.get(0));
        return new PillarChangeMove(pillar, variableDescriptor, oldValue);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector scoreDirector) {
        for (Object entity : pillar) {
            scoreDirector.beforeVariableChanged(variableDescriptor, entity);
            variableDescriptor.setValue(entity, toPlanningValue);
            scoreDirector.afterVariableChanged(variableDescriptor, entity);
        }
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

    public Collection<? extends Object> getPlanningEntities() {
        return pillar;
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toPlanningValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof PillarChangeMove) {
            PillarChangeMove other = (PillarChangeMove) o;
            return new EqualsBuilder()
                    .append(variableDescriptor, other.variableDescriptor)
                    .append(pillar, other.pillar)
                    .append(toPlanningValue, other.toPlanningValue)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(variableDescriptor)
                .append(pillar)
                .append(toPlanningValue)
                .toHashCode();
    }

    public String toString() {
        Object oldValue = variableDescriptor.getValue(pillar.get(0));
        return pillar.toString() + " {" + oldValue + " -> " + toPlanningValue + "}";
    }

}
