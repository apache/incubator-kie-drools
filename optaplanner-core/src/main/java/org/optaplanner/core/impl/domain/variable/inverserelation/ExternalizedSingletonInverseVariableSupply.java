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

package org.optaplanner.core.impl.domain.variable.inverserelation;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.StatefulVariableListener;

/**
 * Alternative to {@link SingletonInverseVariableListener}.
 */
public class ExternalizedSingletonInverseVariableSupply
        implements StatefulVariableListener<Object>, SingletonInverseVariableSupply {

    protected final VariableDescriptor sourceVariableDescriptor;

    protected Map<Object, Object> inverseEntityMap = null;

    public ExternalizedSingletonInverseVariableSupply(VariableDescriptor sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    @Override
    public VariableDescriptor getSourceVariableDescriptor() {
        return sourceVariableDescriptor;
    }

    @Override
    public void resetWorkingSolution(ScoreDirector scoreDirector) {
        EntityDescriptor entityDescriptor = sourceVariableDescriptor.getEntityDescriptor();
        List<Object> entityList = entityDescriptor.extractEntities(scoreDirector.getWorkingSolution());
        inverseEntityMap = new IdentityHashMap<>(entityList.size());
        for (Object entity : entityList) {
            insert(scoreDirector, entity);
        }
    }

    @Override
    public void clearWorkingSolution(ScoreDirector scoreDirector) {
        inverseEntityMap = null;
    }

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Object entity) {
        insert(scoreDirector, entity);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Object entity) {
        retract(scoreDirector, entity);
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Object entity) {
        insert(scoreDirector, entity);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Object entity) {
        retract(scoreDirector, entity);
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Object entity) {
        // Do nothing
    }

    protected void insert(ScoreDirector scoreDirector, Object entity) {
        Object value = sourceVariableDescriptor.getValue(entity);
        if (value == null) {
            return;
        }
        Object oldInverseEntity = inverseEntityMap.put(value, entity);
        if (oldInverseEntity != null) {
            throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                    + " because the entity (" + entity
                    + ") for sourceVariable (" + sourceVariableDescriptor.getVariableName()
                    + ") cannot be inserted: another entity (" + oldInverseEntity
                    + ") already has that value (" + value + ").");
        }
    }

    protected void retract(ScoreDirector scoreDirector, Object entity) {
        Object value = sourceVariableDescriptor.getValue(entity);
        if (value == null) {
            return;
        }
        Object oldInverseEntity = inverseEntityMap.remove(value);
        if (oldInverseEntity != entity) {
            throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                    + " because the entity (" + entity
                    + ") for sourceVariable (" + sourceVariableDescriptor.getVariableName()
                    + ") cannot be retracted: the entity was never inserted for that value (" + value + ").");
        }
    }

    @Override
    public Object getInverseSingleton(Object value) {
        return inverseEntityMap.get(value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getVariableName() + ")";
    }

}
