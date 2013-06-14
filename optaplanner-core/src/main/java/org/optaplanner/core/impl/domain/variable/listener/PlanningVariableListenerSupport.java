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

import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;

public class PlanningVariableListenerSupport {

    private final Map<PlanningVariableDescriptor, PlanningVariableListener> variableListenerMap;

    public PlanningVariableListenerSupport(
            Map<PlanningVariableDescriptor, PlanningVariableListener> variableListenerMap) {
        this.variableListenerMap = variableListenerMap;
    }

    public void beforeEntityAdded(Object entity) {
        for (Map.Entry<PlanningVariableDescriptor, PlanningVariableListener> entry : variableListenerMap.entrySet()) {
            if (entry.getKey().getEntityDescriptor().matchesEntity(entity)) {
                entry.getValue().beforeEntityAdded(entity);
            }
        }
    }

    public void afterEntityAdded(Object entity) {
        for (Map.Entry<PlanningVariableDescriptor, PlanningVariableListener> entry : variableListenerMap.entrySet()) {
            if (entry.getKey().getEntityDescriptor().matchesEntity(entity)) {
                entry.getValue().afterEntityAdded(entity);
            }
        }
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        for (Map.Entry<PlanningVariableDescriptor, PlanningVariableListener> entry : variableListenerMap.entrySet()) {
            if (entry.getKey().matchesEntityVariable(entity, variableName)) {
                entry.getValue().beforeVariableChanged(entity);
            }
        }
    }

    public void afterVariableChanged(Object entity, String variableName) {
        for (Map.Entry<PlanningVariableDescriptor, PlanningVariableListener> entry : variableListenerMap.entrySet()) {
            if (entry.getKey().matchesEntityVariable(entity, variableName)) {
                entry.getValue().afterVariableChanged(entity);
            }
        }
    }

    public void beforeEntityRemoved(Object entity) {
        for (Map.Entry<PlanningVariableDescriptor, PlanningVariableListener> entry : variableListenerMap.entrySet()) {
            if (entry.getKey().getEntityDescriptor().matchesEntity(entity)) {
                entry.getValue().beforeEntityRemoved(entity);
            }
        }
    }

    public void afterEntityRemoved(Object entity) {
        for (Map.Entry<PlanningVariableDescriptor, PlanningVariableListener> entry : variableListenerMap.entrySet()) {
            if (entry.getKey().getEntityDescriptor().matchesEntity(entity)) {
                entry.getValue().afterEntityRemoved(entity);
            }
        }
    }

}
