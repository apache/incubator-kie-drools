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

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.Supply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class VariableListenerSupport implements SupplyManager {

    protected final InnerScoreDirector scoreDirector;

    protected final Map<VariableDescriptor, List<VariableListener>> variableListenerMap;
    protected final Map<EntityDescriptor, List<VariableListener>> entityVariableListenerMap;

    protected final Map<Demand, Supply> supplyMap;

    public VariableListenerSupport(InnerScoreDirector scoreDirector) {
        this.scoreDirector = scoreDirector;
        // Order is important, hence LinkedHashMap
        variableListenerMap = new LinkedHashMap<VariableDescriptor, List<VariableListener>>();
        entityVariableListenerMap = new LinkedHashMap<EntityDescriptor, List<VariableListener>>();
        supplyMap = new LinkedHashMap<Demand, Supply>(variableListenerMap.size());
        for (EntityDescriptor entityDescriptor : scoreDirector.getSolutionDescriptor().getEntityDescriptors()) {
            List<VariableListener> entityVariableListenerList = new ArrayList<VariableListener>();
            for (GenuineVariableDescriptor variableDescriptor : entityDescriptor.getDeclaredGenuineVariableDescriptors()) {
                List<VariableListener> variableListenerList = variableDescriptor
                        .buildVariableListenerListAndRegisterSupply(supplyMap);
                variableListenerMap.put(variableDescriptor, variableListenerList);
                entityVariableListenerList.addAll(variableListenerList);
            }
            for (ShadowVariableDescriptor variableDescriptor : entityDescriptor.getDeclaredShadowVariableDescriptors()) {
                List<VariableListener> variableListenerList = variableDescriptor
                        .buildVariableListenerListAndRegisterSupply(supplyMap);
                variableListenerMap.put(variableDescriptor, variableListenerList);
                entityVariableListenerList.addAll(variableListenerList);
            }
            entityVariableListenerMap.put(entityDescriptor, entityVariableListenerList);
        }
    }

    public <S extends Supply> S demand(Demand<S> demand) {
        S supply = (S) supplyMap.get(demand);
        if (supply == null) {
            supply = demand.createExternalizedSupply();
            if (supply instanceof StatefulVariableListener) {
                StatefulVariableListener statefulVariableListener = (StatefulVariableListener) supply;
                statefulVariableListener.resetWorkingSolution(scoreDirector);
                VariableDescriptor variableDescriptor = statefulVariableListener.getVariableDescriptor();
                List<VariableListener> variableListenerList = variableListenerMap.get(variableDescriptor);
                if (variableListenerList == null) {
                    variableListenerList = new ArrayList<VariableListener>();
                    variableListenerMap.put(variableDescriptor, variableListenerList);
                }
                variableListenerList.add(statefulVariableListener);
                EntityDescriptor entityDescriptor = variableDescriptor.getEntityDescriptor();
                List<VariableListener> entityVariableListenerList = entityVariableListenerMap.get(entityDescriptor);
                if (entityVariableListenerList == null) {
                    entityVariableListenerList = new ArrayList<VariableListener>();
                    entityVariableListenerMap.put(entityDescriptor, entityVariableListenerList);
                }
                entityVariableListenerList.add(statefulVariableListener);
            }
            supplyMap.put(demand, supply);
        }
        return supply;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void resetWorkingSolution(InnerScoreDirector scoreDirector) {
        for (List<VariableListener> variableListenerList : variableListenerMap.values()) {
            for (VariableListener variableListener : variableListenerList) {
                if (variableListener instanceof StatefulVariableListener) {
                    ((StatefulVariableListener) variableListener).resetWorkingSolution(scoreDirector);
                }
            }
        }
    }

    public void clearWorkingSolution(InnerScoreDirector scoreDirector) {
        for (List<VariableListener> variableListenerList : variableListenerMap.values()) {
            for (VariableListener variableListener : variableListenerList) {
                if (variableListener instanceof StatefulVariableListener) {
                    ((StatefulVariableListener) variableListener).clearWorkingSolution(scoreDirector);
                }
            }
        }
    }

    public void beforeEntityAdded(InnerScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (VariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.beforeEntityAdded(scoreDirector, entity);
        }
    }

    public void afterEntityAdded(InnerScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (VariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.afterEntityAdded(scoreDirector, entity);
        }
    }

    public void beforeVariableChanged(InnerScoreDirector scoreDirector, VariableDescriptor variableDescriptor,
            Object entity) {
        for (VariableListener variableListener : variableListenerMap.get(variableDescriptor)) {
            variableListener.beforeVariableChanged(scoreDirector, entity);
        }
    }

    public void afterVariableChanged(InnerScoreDirector scoreDirector, VariableDescriptor variableDescriptor,
            Object entity) {
        for (VariableListener variableListener : variableListenerMap.get(variableDescriptor)) {
            variableListener.afterVariableChanged(scoreDirector, entity);
        }
    }

    public void beforeEntityRemoved(InnerScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (VariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.beforeEntityRemoved(scoreDirector, entity);
        }
    }

    public void afterEntityRemoved(InnerScoreDirector scoreDirector, EntityDescriptor entityDescriptor,
            Object entity) {
        for (VariableListener variableListener : entityVariableListenerMap.get(entityDescriptor)) {
            variableListener.afterEntityRemoved(scoreDirector, entity);
        }
    }

}
