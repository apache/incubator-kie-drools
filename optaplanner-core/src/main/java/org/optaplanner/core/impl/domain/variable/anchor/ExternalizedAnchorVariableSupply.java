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

package org.optaplanner.core.impl.domain.variable.anchor;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.listener.StatefulVariableListener;

/**
 * Alternative to {@link AnchorVariableListener}.
 */
public class ExternalizedAnchorVariableSupply implements StatefulVariableListener<Object>, AnchorVariableSupply {

    protected final VariableDescriptor previousVariableDescriptor;
    protected final SingletonInverseVariableSupply nextVariableSupply;

    protected Map<Object, Object> anchorMap = null;

    public ExternalizedAnchorVariableSupply(VariableDescriptor previousVariableDescriptor,
            SingletonInverseVariableSupply nextVariableSupply) {
        this.previousVariableDescriptor = previousVariableDescriptor;
        this.nextVariableSupply = nextVariableSupply;
    }

    @Override
    public VariableDescriptor getSourceVariableDescriptor() {
        return previousVariableDescriptor;
    }

    @Override
    public void resetWorkingSolution(ScoreDirector scoreDirector) {
        EntityDescriptor entityDescriptor = previousVariableDescriptor.getEntityDescriptor();
        List<Object> entityList = entityDescriptor.extractEntities(scoreDirector.getWorkingSolution());
        anchorMap = new IdentityHashMap<>(entityList.size());
        for (Object entity : entityList) {
            insert(scoreDirector, entity);
        }
    }

    @Override
    public void clearWorkingSolution(ScoreDirector scoreDirector) {
        anchorMap = null;
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
        // No need to retract() because the insert (which is guaranteed to be called later) affects the same trailing entities.
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Object entity) {
        insert(scoreDirector, entity);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Object entity) {
        boolean removeSucceeded = anchorMap.remove(entity) != null;
        if (!removeSucceeded) {
            throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                    + " because the entity (" + entity
                    + ") for sourceVariable (" + previousVariableDescriptor.getVariableName()
                    + ") cannot be retracted: it was never inserted.");
        }
        // No need to retract the trailing entities because they will be removed too or change their previousVariable
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Object entity) {
        // Do nothing
    }

    protected void insert(ScoreDirector scoreDirector, Object entity) {
        Object previousEntity = previousVariableDescriptor.getValue(entity);
        Object anchor;
        if (previousEntity == null) {
            anchor = null;
        } else if (previousVariableDescriptor.isValuePotentialAnchor(previousEntity)) {
            anchor = previousEntity;
        } else {
            anchor = anchorMap.get(previousEntity);
        }
        Object nextEntity = entity;
        while (nextEntity != null && anchorMap.get(nextEntity) != anchor) {
            anchorMap.put(nextEntity, anchor);
            nextEntity = nextVariableSupply.getInverseSingleton(nextEntity);
        }
    }

    @Override
    public Object getAnchor(Object entity) {
        return anchorMap.get(entity);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + previousVariableDescriptor.getVariableName() + ")";
    }

}
