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

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.StatefulVariableListener;

/**
 * Alternative to {@link CollectionInverseVariableListener}.
 */
public class ExternalizedCollectionInverseVariableSupply
        implements StatefulVariableListener<Object>, CollectionInverseVariableSupply {

    protected final VariableDescriptor sourceVariableDescriptor;

    protected Map<Object, Set<Object>> inverseEntitySetMap = null;

    public ExternalizedCollectionInverseVariableSupply(VariableDescriptor sourceVariableDescriptor) {
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
        inverseEntitySetMap = new IdentityHashMap<>(entityList.size());
        for (Object entity : entityList) {
            insert(scoreDirector, entity);
        }
    }

    @Override
    public void clearWorkingSolution(ScoreDirector scoreDirector) {
        inverseEntitySetMap = null;
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
        Set<Object> inverseEntitySet = inverseEntitySetMap.computeIfAbsent(value,
                k -> Collections.newSetFromMap(new IdentityHashMap<>()));
        boolean addSucceeded = inverseEntitySet.add(entity);
        if (!addSucceeded) {
            throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                    + " because the entity (" + entity
                    + ") for sourceVariable (" + sourceVariableDescriptor.getVariableName()
                    + ") cannot be inserted: it was already inserted.");
        }
    }

    protected void retract(ScoreDirector scoreDirector, Object entity) {
        Object value = sourceVariableDescriptor.getValue(entity);
        if (value == null) {
            return;
        }
        Set<Object> inverseEntitySet = inverseEntitySetMap.get(value);
        boolean removeSucceeded = inverseEntitySet.remove(entity);
        if (!removeSucceeded) {
            throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                    + " because the entity (" + entity
                    + ") for sourceVariable (" + sourceVariableDescriptor.getVariableName()
                    + ") cannot be retracted: it was never inserted.");
        }
        if (inverseEntitySet.isEmpty()) {
            inverseEntitySetMap.put(value, null);
        }
    }

    @Override
    public Collection<?> getInverseCollection(Object value) {
        Set<Object> inverseEntitySet = inverseEntitySetMap.get(value);
        if (inverseEntitySet == null) {
            return Collections.emptySet();
        }
        return inverseEntitySet;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getVariableName() + ")";
    }

}
