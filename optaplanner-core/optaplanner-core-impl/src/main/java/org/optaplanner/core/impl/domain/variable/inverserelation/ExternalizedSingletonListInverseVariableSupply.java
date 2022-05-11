/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.variable.inverserelation;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.ListVariableListener;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.SourcedVariableListener;

/**
 * Alternative to {@link SingletonListInverseVariableListener}.
 */
public class ExternalizedSingletonListInverseVariableSupply<Solution_> implements
        SourcedVariableListener<Solution_>,
        ListVariableListener<Solution_, Object>,
        SingletonInverseVariableSupply {

    protected final ListVariableDescriptor<Solution_> sourceVariableDescriptor;

    protected Map<Object, Object> inverseEntityMap = null;

    public ExternalizedSingletonListInverseVariableSupply(ListVariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    @Override
    public VariableDescriptor<Solution_> getSourceVariableDescriptor() {
        return sourceVariableDescriptor;
    }

    @Override
    public void resetWorkingSolution(ScoreDirector<Solution_> scoreDirector) {
        inverseEntityMap = new IdentityHashMap<>();
        sourceVariableDescriptor.getEntityDescriptor().getSolutionDescriptor()
                .visitAllEntities(scoreDirector.getWorkingSolution(), this::insert);
    }

    @Override
    public void close() {
        inverseEntityMap = null;
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
    public void beforeElementAdded(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        // Do nothing
    }

    @Override
    public void afterElementAdded(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        Object element = sourceVariableDescriptor.getElement(entity, index);
        Object oldInverseEntity = inverseEntityMap.put(element, entity);
        if (oldInverseEntity != null) {
            throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                    + " because the element (" + element
                    + ") has an oldInverseEntity (" + oldInverseEntity
                    + ") which is not null.");
        }
    }

    @Override
    public void beforeElementRemoved(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        Object element = sourceVariableDescriptor.getElement(entity, index);
        Object oldInverseEntity = inverseEntityMap.remove(element);
        if (oldInverseEntity == null) {
            throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                    + " because the element (" + element
                    + ") has an oldInverseEntity (" + oldInverseEntity
                    + ") which is not set.");
        }
    }

    @Override
    public void afterElementRemoved(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        // Do nothing
    }

    @Override
    public void beforeElementMoved(ScoreDirector<Solution_> scoreDirector,
            Object sourceEntity, int sourceIndex, Object destinationEntity, int destinationIndex) {
        // Do nothing
    }

    @Override
    public void afterElementMoved(ScoreDirector<Solution_> scoreDirector,
            Object sourceEntity, int sourceIndex, Object destinationEntity, int destinationIndex) {
        Object element = sourceVariableDescriptor.getElement(destinationEntity, destinationIndex);
        Object oldInverseEntity = inverseEntityMap.put(element, destinationEntity);
        if (oldInverseEntity != sourceEntity) {
            throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                    + " because the element (" + element
                    + ") has an oldInverseEntity (" + oldInverseEntity
                    + ") which is not the sourceEntity (" + sourceEntity + ").");
        }
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // When the entity is removed, its values become unassigned. An unassigned value has no inverse entity and no index.
        for (Object element : sourceVariableDescriptor.getListVariable(entity)) {
            inverseEntityMap.remove(element);
        }
    }

    private void insert(Object entity) {
        List<Object> listVariable = sourceVariableDescriptor.getListVariable(entity);
        for (Object element : listVariable) {
            Object oldInverseEntity = inverseEntityMap.put(element, entity);
            if (oldInverseEntity != null) {
                throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                        + " because the element (" + element
                        + ") has an oldInverseEntity (" + oldInverseEntity
                        + ") which is not null.");
            }
        }
    }

    @Override
    public Object getInverseSingleton(Object element) {
        return inverseEntityMap.get(element);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getVariableName() + ")";
    }
}
