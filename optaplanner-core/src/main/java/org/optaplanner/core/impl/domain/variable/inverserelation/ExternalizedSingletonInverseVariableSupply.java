/*
 * Copyright 2014 JBoss Inc
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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.StatefulVariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Alternative to {@link SingletonInverseVariableListener}.
 */
public class ExternalizedSingletonInverseVariableSupply implements StatefulVariableListener<Object>, SingletonInverseVariableSupply {

    protected final VariableDescriptor sourceVariableDescriptor;

    protected Map<Object, Set<Object>> inverseEntitySetMap = null;

    public ExternalizedSingletonInverseVariableSupply(VariableDescriptor sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    public void resetWorkingSolution(ScoreDirector scoreDirector, Solution workingSolution) {
        EntityDescriptor entityDescriptor = sourceVariableDescriptor.getEntityDescriptor();
        List<Object> entityList = entityDescriptor.extractEntities(workingSolution);
        inverseEntitySetMap = new IdentityHashMap<Object, Set<Object>>(entityList.size());
        for (Object entity : entityList) {
            insert(scoreDirector, entity);
        }
    }

    public void clearWorkingSolution(ScoreDirector scoreDirector) {
        inverseEntitySetMap = null;
    }

    public void beforeEntityAdded(ScoreDirector scoreDirector, Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, Object entity) {
        insert(scoreDirector, entity);
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, Object entity) {
        retract(scoreDirector, entity);
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, Object entity) {
        insert(scoreDirector, entity);
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, Object entity) {
        retract(scoreDirector, entity);
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, Object entity) {
        // Do nothing
    }

    protected void insert(ScoreDirector scoreDirector, Object entity) {
        Object value = sourceVariableDescriptor.getValue(entity);
        Set<Object> inverseEntitySet = inverseEntitySetMap.get(value);
        if (inverseEntitySet == null) {
            inverseEntitySet = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
            inverseEntitySetMap.put(value, inverseEntitySet);
        }
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

    public Object getInverseSingleton(Object value) {
        Set<Object> inverseEntitySet = inverseEntitySetMap.get(value);
        if (inverseEntitySet == null) {
            return null;
        }
        // inverseEntitySet can never be an empty list
        if (inverseEntitySet.size() > 1) {
            throw new IllegalStateException("The planning value (" + value
                    + ") has multiple inverse entities (" + inverseEntitySet
                    + ") pointing to it from sourceVariable (" + sourceVariableDescriptor.getVariableName() + ").");
        }
        return inverseEntitySet.iterator().next();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getVariableName() + ")";
    }

}
