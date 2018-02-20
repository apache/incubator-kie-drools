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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ChangeMove<Solution_> extends AbstractMove<Solution_> {

    protected final Object entity;
    protected final GenuineVariableDescriptor<Solution_> variableDescriptor;
    protected final Object toPlanningValue;

    public ChangeMove(Object entity, GenuineVariableDescriptor<Solution_> variableDescriptor,
            Object toPlanningValue) {
        this.entity = entity;
        this.variableDescriptor = variableDescriptor;
        this.toPlanningValue = toPlanningValue;
    }

    public Object getEntity() {
        return entity;
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
        Object oldValue = variableDescriptor.getValue(entity);
        return !Objects.equals(oldValue, toPlanningValue);
    }

    @Override
    public ChangeMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        Object oldValue = variableDescriptor.getValue(entity);
        return new ChangeMove<>(entity, variableDescriptor, oldValue);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        scoreDirector.beforeVariableChanged(variableDescriptor, entity);
        variableDescriptor.setValue(entity, toPlanningValue);
        scoreDirector.afterVariableChanged(variableDescriptor, entity);
    }

    @Override
    public ChangeMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new ChangeMove<>(destinationScoreDirector.lookUpWorkingObject(entity),
                variableDescriptor,
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
        return Collections.singletonList(entity);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toPlanningValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ChangeMove) {
            ChangeMove<?> other = (ChangeMove) o;
            return new EqualsBuilder()
                    .append(entity, other.entity)
                    .append(variableDescriptor, other.variableDescriptor)
                    .append(toPlanningValue, other.toPlanningValue)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(entity)
                .append(variableDescriptor)
                .append(toPlanningValue)
                .toHashCode();
    }

    @Override
    public String toString() {
        Object oldValue = variableDescriptor.getValue(entity);
        return entity + " {" + oldValue + " -> " + toPlanningValue + "}";
    }

}
