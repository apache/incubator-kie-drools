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

package org.optaplanner.core.impl.heuristic.selector.entity.pillar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelector;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * This is the common {@link PillarSelector} implementation.
 */
public class SameValuePillarSelector extends AbstractSelector
        implements PillarSelector, SelectionCacheLifecycleListener {

    protected static final SelectionCacheType CACHE_TYPE = SelectionCacheType.STEP;

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
                throw new IllegalStateException("The selector (" + this
                        + ") has a variableDescriptor (" + variableDescriptor
                        + ") with a entityClass (" + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                        + ") which is not equal to the entitySelector's entityClass (" + entityClass + ").");
            }
            if (variableDescriptor.isChained()) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a variableDescriptor (" + variableDescriptor
                        + ") which is chained (" + variableDescriptor.isChained() + ").");
            }
        }
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            if (variableDescriptor.isChained()) {
                throw new IllegalStateException("The selector (" + this
                        + ") cannot have a variableDescriptor (" + variableDescriptor
                        + ") which is chained (" + variableDescriptor.isChained() + ").");
            }
        }
        if (entitySelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has an entitySelector (" + entitySelector
                    + ") with neverEnding (" + entitySelector.isNeverEnding() + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(entitySelector);
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(CACHE_TYPE, this));
    }

    public PlanningEntityDescriptor getEntityDescriptor() {
        return entitySelector.getEntityDescriptor();
    }

    @Override
    public SelectionCacheType getCacheType() {
        return CACHE_TYPE;
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
        // CachedListRandomIterator is neverEnding
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
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    public ListIterator<List<Object>> listIterator(int index) {
        if (!randomSelection) {
            return cachedPillarList.listIterator(index);
        } else {
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelector + ")";
    }

}
