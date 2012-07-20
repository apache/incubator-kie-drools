/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.heuristic.selector.entity.pillar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.AbstractSelector;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * This is the common {@link PillarSelector} implementation.
 */
public class SameValuePillarSelector extends AbstractSelector
        implements PillarSelector, SelectionCacheLifecycleListener {

    protected final EntitySelector entitySelector;
    protected final Collection<PlanningVariableDescriptor> variableDescriptors;
    protected final boolean randomSelection;

    protected List<List<Object>> cachedPillarList = null;

    public SameValuePillarSelector(EntitySelector entitySelector,
            Collection<PlanningVariableDescriptor> variableDescriptors, boolean randomSelection) {
        this.entitySelector = entitySelector;
        this.variableDescriptors = variableDescriptors;
        this.randomSelection = randomSelection;
        Class<?> entityClass = entitySelector.getEntityDescriptor().getPlanningEntityClass();
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            if (!entityClass.equals(
                    variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass())) {
                throw new IllegalStateException("The moveSelector (" + this.getClass()
                        + ") has a variableDescriptor (" + variableDescriptor + ") with a planningEntityClass ("
                        + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                        + ") which is not equal to the entitySelector's planningEntityClass ("
                        + entityClass + ").");
            }
            if (variableDescriptor.isChained()) {
                throw new IllegalStateException("The pillarSelector (" + this.getClass()
                        + ") has a variableDescriptor (" + variableDescriptor
                        + ") which is chained (" + variableDescriptor.isChained() + ").");
            }
        }
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            if (variableDescriptor.isChained()) {
                throw new IllegalStateException("The pillarSelector (" + this.getClass()
                        + ") cannot have a variableDescriptor (" + variableDescriptor
                        + ") which is chained (" + variableDescriptor.isChained() + ").");
            }
        }
        if (entitySelector.isNeverEnding()) {
            throw new IllegalStateException("The entitySelector (" + entitySelector + ") has neverEnding ("
                    + entitySelector.isNeverEnding() + ") on a class (" + getClass().getName() + ") instance.");
        }
        solverPhaseLifecycleSupport.addEventListener(entitySelector);
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(SelectionCacheType.STEP, this));
    }

    public PlanningEntityDescriptor getEntityDescriptor() {
        return entitySelector.getEntityDescriptor();
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        long entitySize = entitySelector.getSize();
        if (entitySize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The subChainSelector (" + this + ") has an entitySelector ("
                    + entitySelector + ") with entitySize (" + entitySize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        Map<List<Object>, List<Object>> valueStateToPillarMap = new LinkedHashMap<List<Object>, List<Object>>((int) entitySize);
        for (Object entity : entitySelector) {
            List<Object> valueState = new ArrayList<Object>(variableDescriptors.size());
            for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
                Object value = variableDescriptor.getValue(entity);
                valueState.add(value);
            }
            List<Object> pillar = valueStateToPillarMap.get(valueState);
            if (pillar == null) {
                pillar = new ArrayList<Object>();
                valueStateToPillarMap.put(valueState, pillar);
            }
            pillar.add(entity);
        }
        cachedPillarList = new ArrayList<List<Object>>(valueStateToPillarMap.values());
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedPillarList = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return randomSelection;
    }

    public long getSize() {
        return (long) cachedPillarList.size();
    }

    public Iterator<List<Object>> iterator() {
        if (!randomSelection) {
            return cachedPillarList.iterator();
        } else {
            return new CachedListRandomIterator<List<Object>>(cachedPillarList, workingRandom);
        }
    }

    public ListIterator<List<Object>> listIterator() {
        if (!randomSelection) {
            return cachedPillarList.listIterator();
        } else {
            throw new IllegalStateException("ListIterator is not supported with randomSelection ("
                    + randomSelection + ").");
        }
    }

    public ListIterator<List<Object>> listIterator(int index) {
        if (!randomSelection) {
            return cachedPillarList.listIterator(index);
        } else {
            throw new IllegalStateException("ListIterator is not supported with randomSelection ("
                    + randomSelection + ").");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelector + ")";
    }

}
