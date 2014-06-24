/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.domain.variable.listener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class VariableListenerSupport {

    private final Map<VariableDescriptor, List<VariableListener>> variableListenerMap;
    private final Map<EntityDescriptor, List<VariableListener>> entityVariableListenerMap;

    public VariableListenerSupport(
            Map<VariableDescriptor, List<VariableListener>> variableListenerMap) {
        this.variableListenerMap = variableListenerMap;
        entityVariableListenerMap = new LinkedHashMap<EntityDescriptor, List<VariableListener>>(
                variableListenerMap.size());
        for (Map.Entry<VariableDescriptor, List<VariableListener>> entry
                : variableListenerMap.entrySet()) {
            EntityDescriptor entityDescriptor = entry.getKey().getEntityDescriptor();
            List<VariableListener> variableListenerList = entry.getValue();
            List<VariableListener> entityVariableListenerList = entityVariableListenerMap.get(entityDescriptor);
            if (entityVariableListenerList == null) {
                entityVariableListenerList = new ArrayList<VariableListener>(variableListenerList);
                entityVariableListenerMap.put(entityDescriptor, entityVariableListenerList);
            } else {
                entityVariableListenerList.addAll(variableListenerList);
            }
        }
    }

    public void beforeEntityAdded(ScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (VariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.beforeEntityAdded(scoreDirector, entity);
        }
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (VariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.afterEntityAdded(scoreDirector, entity);
        }
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, VariableDescriptor variableDescriptor,
            Object entity) {
        if (variableDescriptor.hasAnyShadow()) {
            for (VariableListener variableListener : variableListenerMap.get(variableDescriptor)) {
                variableListener.beforeVariableChanged(scoreDirector, entity);
            }
        }
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, VariableDescriptor variableDescriptor,
            Object entity) {
        if (variableDescriptor.hasAnyShadow()) {
            for (VariableListener variableListener : variableListenerMap.get(variableDescriptor)) {
                variableListener.afterVariableChanged(scoreDirector, entity);
            }
        }
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (VariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.beforeEntityRemoved(scoreDirector, entity);
        }
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (VariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.afterEntityRemoved(scoreDirector, entity);
        }
    }

}
