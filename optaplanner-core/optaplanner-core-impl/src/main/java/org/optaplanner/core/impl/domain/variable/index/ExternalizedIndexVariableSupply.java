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

package org.optaplanner.core.impl.domain.variable.index;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.SourcedVariableListener;

/**
 * Alternative to {@link IndexVariableListener}.
 */
public class ExternalizedIndexVariableSupply<Solution_>
        implements SourcedVariableListener<Solution_, Object>, IndexVariableSupply {

    protected final ListVariableDescriptor<Solution_> sourceVariableDescriptor;

    protected Map<Object, Integer> indexMap = null;

    public ExternalizedIndexVariableSupply(ListVariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    @Override
    public VariableDescriptor<Solution_> getSourceVariableDescriptor() {
        return sourceVariableDescriptor;
    }

    @Override
    public void resetWorkingSolution(ScoreDirector<Solution_> scoreDirector) {
        indexMap = new IdentityHashMap<>();
        sourceVariableDescriptor.getEntityDescriptor().getSolutionDescriptor()
                .visitAllEntities(scoreDirector.getWorkingSolution(), this::insert);
    }

    @Override
    public void close() {
        indexMap = null;
    }

    @Override
    public boolean requiresUniqueEntityEvents() {
        // A move on a single entity produces multiple before/after variable changed events for the given entity
        // but the corrupted supply checks in insert/retract methods require a unique pair of before/after events.
        return true;
    }

    @Override
    public void beforeEntityAdded(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<Solution_> scoreDirector, Object entity) {
        insert(entity);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<Solution_> scoreDirector, Object entity) {
        retract(entity);
    }

    @Override
    public void afterVariableChanged(ScoreDirector<Solution_> scoreDirector, Object entity) {
        insert(entity);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // When the entity is removed, its values become unassigned. An unassigned value has no inverse entity and no index.
        retract(entity);
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // Do nothing
    }

    private void insert(Object entity) {
        List<Object> listVariable = sourceVariableDescriptor.getListVariable(entity);
        int index = 0;
        for (Object value : listVariable) {
            Integer oldIndex = indexMap.put(value, index);
            if (oldIndex != null) {
                throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                        + " because the entity (" + entity
                        + ") for sourceVariable (" + sourceVariableDescriptor.getVariableName()
                        + ") cannot be inserted: one of its values (" + value
                        + ") at index (" + index + ") already has a non-null oldIndex (" + oldIndex + ").");
            }
            index++;
        }
    }

    private void retract(Object entity) {
        List<Object> listVariable = sourceVariableDescriptor.getListVariable(entity);
        int index = 0;
        for (Object value : listVariable) {
            Integer oldIndex = indexMap.remove(value);
            if (!Objects.equals(oldIndex, index)) {
                throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                        + " because the entity (" + entity
                        + ") for sourceVariable (" + sourceVariableDescriptor.getVariableName()
                        + ") cannot be retracted: one of its values (" + value
                        + ") at index (" + index + ") already has an unexpected oldIndex (" + oldIndex + ").");
            }
            index++;
        }
    }

    @Override
    public Integer getIndex(Object planningValue) {
        return indexMap.get(planningValue);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getVariableName() + ")";
    }
}
