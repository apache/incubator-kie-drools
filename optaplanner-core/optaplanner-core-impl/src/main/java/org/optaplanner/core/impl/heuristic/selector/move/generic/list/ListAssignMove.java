/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class ListAssignMove<Solution_> extends AbstractMove<Solution_> {

    private final ListVariableDescriptor<Solution_> variableDescriptor;
    private final Object planningValue;
    private final Object destinationEntity;
    private final int destinationIndex;

    public ListAssignMove(
            ListVariableDescriptor<Solution_> variableDescriptor,
            Object planningValue,
            Object destinationEntity, int destinationIndex) {
        this.variableDescriptor = variableDescriptor;
        this.planningValue = planningValue;
        this.destinationEntity = destinationEntity;
        this.destinationIndex = destinationIndex;
    }

    public Object getDestinationEntity() {
        return destinationEntity;
    }

    public int getDestinationIndex() {
        return destinationIndex;
    }

    public Object getMovedValue() {
        return planningValue;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        return true;
    }

    @Override
    public ListUnassignMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        return new ListUnassignMove<>(variableDescriptor, destinationEntity, destinationIndex);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;
        // Add planningValue to destinationEntity's list variable (at destinationIndex).
        innerScoreDirector.beforeElementAdded(variableDescriptor, destinationEntity, destinationIndex);
        variableDescriptor.addElement(destinationEntity, destinationIndex, planningValue);
        innerScoreDirector.afterElementAdded(variableDescriptor, destinationEntity, destinationIndex);
    }

    @Override
    public ListAssignMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new ListAssignMove<>(
                variableDescriptor,
                destinationScoreDirector.lookUpWorkingObject(planningValue),
                destinationScoreDirector.lookUpWorkingObject(destinationEntity),
                destinationIndex);
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListAssignMove<?> other = (ListAssignMove<?>) o;
        return destinationIndex == other.destinationIndex
                && Objects.equals(variableDescriptor, other.variableDescriptor)
                && Objects.equals(planningValue, other.planningValue)
                && Objects.equals(destinationEntity, other.destinationEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableDescriptor, planningValue, destinationEntity, destinationIndex);
    }

    @Override
    public String toString() {
        return String.format("%s {null -> %s[%d]}",
                getMovedValue(), destinationEntity, destinationIndex);
    }
}
