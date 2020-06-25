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

import java.util.Collection;
import java.util.Collections;
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
public class PillarChangeMove<Solution_> extends AbstractMove<Solution_> {

    protected final GenuineVariableDescriptor<Solution_> variableDescriptor;

    protected final List<Object> pillar;
    protected final Object toPlanningValue;

    public PillarChangeMove(List<Object> pillar, GenuineVariableDescriptor<Solution_> variableDescriptor,
            Object toPlanningValue) {
        this.pillar = pillar;
        this.variableDescriptor = variableDescriptor;
        this.toPlanningValue = toPlanningValue;
    }

    public List<Object> getPillar() {
        return pillar;
    }

    public String getVariableName() {
        return variableDescriptor.getVariableName();
    }

    public Object getToPlanningValue() {
        return toPlanningValue;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        Object oldValue = variableDescriptor.getValue(pillar.get(0));
        if (Objects.equals(oldValue, toPlanningValue)) {
            return false;
        }
        if (!variableDescriptor.isValueRangeEntityIndependent()) {
            ValueRangeDescriptor<Solution_> valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
            Solution_ workingSolution = scoreDirector.getWorkingSolution();
            for (Object entity : pillar) {
                ValueRange rightValueRange = valueRangeDescriptor.extractValueRange(workingSolution, entity);
                if (!rightValueRange.contains(toPlanningValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public PillarChangeMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        Object oldValue = variableDescriptor.getValue(pillar.get(0));
        return new PillarChangeMove<>(pillar, variableDescriptor, oldValue);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        InnerScoreDirector<Solution_> innerScoreDirector = (InnerScoreDirector<Solution_>) scoreDirector;
        for (Object entity : pillar) {
            innerScoreDirector.beforeVariableChanged(variableDescriptor, entity);
            variableDescriptor.setValue(entity, toPlanningValue);
            innerScoreDirector.afterVariableChanged(variableDescriptor, entity);
        }
    }

    @Override
    public PillarChangeMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new PillarChangeMove<>(rebaseList(pillar, destinationScoreDirector), variableDescriptor,
                destinationScoreDirector.lookUpWorkingObject(toPlanningValue));
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return pillar;
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toPlanningValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PillarChangeMove<?> other = (PillarChangeMove<?>) o;
        return Objects.equals(variableDescriptor, other.variableDescriptor) &&
                Objects.equals(pillar, other.pillar) &&
                Objects.equals(toPlanningValue, other.toPlanningValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableDescriptor, pillar, toPlanningValue);
    }

    @Override
    public String toString() {
        Object oldValue = variableDescriptor.getValue(pillar.get(0));
        return pillar.toString() + " {" + oldValue + " -> " + toPlanningValue + "}";
    }

}
