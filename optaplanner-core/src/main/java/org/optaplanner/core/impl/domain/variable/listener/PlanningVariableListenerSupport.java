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
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class PlanningVariableListenerSupport {

    private final Map<GenuineVariableDescriptor, List<PlanningVariableListener>> variableListenerMap;
    private final Map<EntityDescriptor, List<PlanningVariableListener>> entityVariableListenerMap;

    public PlanningVariableListenerSupport(
            Map<GenuineVariableDescriptor, List<PlanningVariableListener>> variableListenerMap) {
        this.variableListenerMap = variableListenerMap;
        entityVariableListenerMap = new LinkedHashMap<EntityDescriptor, List<PlanningVariableListener>>(
                variableListenerMap.size());
        for (Map.Entry<GenuineVariableDescriptor, List<PlanningVariableListener>> entry
                : variableListenerMap.entrySet()) {
            EntityDescriptor entityDescriptor = entry.getKey().getEntityDescriptor();
            List<PlanningVariableListener> variableListenerList = entry.getValue();
            List<PlanningVariableListener> entityVariableListenerList = entityVariableListenerMap.get(entityDescriptor);
            if (entityVariableListenerList == null) {
                entityVariableListenerList = new ArrayList<PlanningVariableListener>(variableListenerList);
                entityVariableListenerMap.put(entityDescriptor, entityVariableListenerList);
            } else {
                entityVariableListenerList.addAll(variableListenerList);
            }
        }
    }

    public void beforeEntityAdded(ScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (PlanningVariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.beforeEntityAdded(scoreDirector, entity);
        }
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (PlanningVariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.afterEntityAdded(scoreDirector, entity);
        }
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, GenuineVariableDescriptor variableDescriptor,
            Object entity) {
        for (PlanningVariableListener variableListener : variableListenerMap.get(variableDescriptor)) {
            variableListener.beforeVariableChanged(scoreDirector, entity);
        }
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, GenuineVariableDescriptor variableDescriptor,
            Object entity) {
        for (PlanningVariableListener variableListener : variableListenerMap.get(variableDescriptor)) {
            variableListener.afterVariableChanged(scoreDirector, entity);
        }
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (PlanningVariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.beforeEntityRemoved(scoreDirector, entity);
        }
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (PlanningVariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.afterEntityRemoved(scoreDirector, entity);
        }
    }

}
