/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelector;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.DefaultSubChainSelector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * @see PillarSelector
 */
public class DefaultPillarSelector extends AbstractSelector
        implements PillarSelector, SelectionCacheLifecycleListener {

    protected static final SelectionCacheType CACHE_TYPE = SelectionCacheType.STEP;

    protected final EntitySelector entitySelector;
    protected final Collection<GenuineVariableDescriptor> variableDescriptors;
    protected final boolean randomSelection;

    protected final boolean subPillarEnabled;
    /**
     * Unlike {@link DefaultSubChainSelector#minimumSubChainSize} and {@link DefaultSubChainSelector#maximumSubChainSize},
     * the sub selection here is any sub set. For example from ABCDE, it can select BCD and also ACD.
     */
    protected final int minimumSubPillarSize;
    protected final int maximumSubPillarSize;

    protected List<List<Object>> cachedBasePillarList = null;

    public DefaultPillarSelector(EntitySelector entitySelector,
            Collection<GenuineVariableDescriptor> variableDescriptors, boolean randomSelection,
            boolean subPillarEnabled, int minimumSubPillarSize, int maximumSubPillarSize) {
        this.entitySelector = entitySelector;
        this.variableDescriptors = variableDescriptors;
        this.randomSelection = randomSelection;
        Class<?> entityClass = entitySelector.getEntityDescriptor().getEntityClass();
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            if (!entityClass.equals(
                    variableDescriptor.getEntityDescriptor().getEntityClass())) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a variableDescriptor (" + variableDescriptor
                        + ") with a entityClass (" + variableDescriptor.getEntityDescriptor().getEntityClass()
                        + ") which is not equal to the entitySelector's entityClass (" + entityClass + ").");
            }
            if (variableDescriptor.isChained()) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a variableDescriptor (" + variableDescriptor
                        + ") which is chained (" + variableDescriptor.isChained() + ").");
            }
        }
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
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
        phaseLifecycleSupport.addEventListener(entitySelector);
        phaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(CACHE_TYPE, this));
        this.subPillarEnabled = subPillarEnabled;
        this.minimumSubPillarSize = minimumSubPillarSize;
        this.maximumSubPillarSize = maximumSubPillarSize;
        if (minimumSubPillarSize < 1) {
            throw new IllegalStateException("The selector (" + this
                    + ")'s minimumPillarSize (" + minimumSubPillarSize
                    + ") must be at least 1.");
        }
        if (minimumSubPillarSize > maximumSubPillarSize) {
            throw new IllegalStateException("The minimumPillarSize (" + minimumSubPillarSize
                    + ") must be at least maximumSubChainSize (" + maximumSubPillarSize + ").");
        }
        if (!randomSelection && subPillarEnabled) {
            throw new IllegalStateException("The selector (" + this
                    + ") with randomSelection  (" + randomSelection + ") and subPillarEnabled (" + subPillarEnabled
                    + ") does not support non random selection with sub pillars" +
                    " because the number of sub pillars scales exponentially.\n"
                    + "Either set subPillarEnabled to false or use JIT random selection.");
        }
    }

    @Override
    public EntityDescriptor getEntityDescriptor() {
        return entitySelector.getEntityDescriptor();
    }

    @Override
    public SelectionCacheType getCacheType() {
        return CACHE_TYPE;
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    @Override
    public void constructCache(DefaultSolverScope solverScope) {
        long entitySize = entitySelector.getSize();
        if (entitySize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this + ") has an entitySelector ("
                    + entitySelector + ") with entitySize (" + entitySize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        Map<List<Object>, List<Object>> valueStateToPillarMap = new LinkedHashMap<>((int) entitySize);
        for (Object entity : entitySelector) {
            List<Object> valueState = new ArrayList<>(variableDescriptors.size());
            for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
                Object value = variableDescriptor.getValue(entity);
                valueState.add(value);
            }
            List<Object> pillar = valueStateToPillarMap.computeIfAbsent(valueState, key -> new ArrayList<>());
            pillar.add(entity);
        }
        cachedBasePillarList = new ArrayList<>(valueStateToPillarMap.values());
    }

    @Override
    public void disposeCache(DefaultSolverScope solverScope) {
        cachedBasePillarList = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        // CachedListRandomIterator is neverEnding
        return randomSelection;
    }

    @Override
    public long getSize() {
        if (!subPillarEnabled) {
            return (long) cachedBasePillarList.size();
        } else {
            // For each pillar, the number of combinations is: the sum of every (n! / (k! (n-k)!))
            // for which n is pillar.getSize() and k iterates from minimumSubPillarSize to maximumSubPillarSize
            // This implies that a single pillar of size 64 is already too big to be held in a long
            throw new UnsupportedOperationException("The selector (" + this
                    + ") with randomSelection  (" + randomSelection + ") and subPillarEnabled (" + subPillarEnabled
                    + ") does not support getSize()" +
                    " because the number of sub pillars scales exponentially.");
        }
    }

    @Override
    public Iterator<List<Object>> iterator() {
        if (!randomSelection) {
            if (!subPillarEnabled) {
                return cachedBasePillarList.iterator();
            } else {
                throw new IllegalStateException(
                        "Impossible state because the constructors fails with randomSelection (" + randomSelection
                                + ") and subPillarEnabled (" + subPillarEnabled + ").");
            }
        } else {
            if (!subPillarEnabled) {
                return new CachedListRandomIterator<>(cachedBasePillarList, workingRandom);
            } else {
                return new RandomSubPillarIterator();
            }
        }
    }

    @Override
    public ListIterator<List<Object>> listIterator() {
        if (!randomSelection) {
            if (!subPillarEnabled) {
                return cachedBasePillarList.listIterator();
            } else {
                throw new IllegalStateException(
                        "Impossible state because the constructors fails with randomSelection (" + randomSelection
                                + ") and subPillarEnabled (" + subPillarEnabled + ").");
            }
        } else {
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    @Override
    public ListIterator<List<Object>> listIterator(int index) {
        if (!randomSelection) {
            if (!subPillarEnabled) {
                return cachedBasePillarList.listIterator(index);
            } else {
                throw new IllegalStateException(
                        "Impossible state because the constructors fails with randomSelection (" + randomSelection
                                + ") and subPillarEnabled (" + subPillarEnabled + ").");
            }
        } else {
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelector + ")";
    }

    private class RandomSubPillarIterator extends UpcomingSelectionIterator<List<Object>> {

        public RandomSubPillarIterator() {
            if (cachedBasePillarList.isEmpty()) {
                upcomingSelection = noUpcomingSelection();
                upcomingCreated = true;
            }
        }

        @Override
        protected List<Object> createUpcomingSelection() {
            List<Object> basePillar = selectBasePillar();
            // Known issue/compromise: Every subPillar should have same probability, but doesn't.
            // Instead, every subPillar size has the same probability.
            int basePillarSize = basePillar.size();
            int min = (minimumSubPillarSize > basePillarSize) ? basePillarSize : minimumSubPillarSize;
            int max = (maximumSubPillarSize > basePillarSize) ? basePillarSize : maximumSubPillarSize;
            int subPillarSize = min + workingRandom.nextInt(max - min + 1);
            // Random sampling: See http://eyalsch.wordpress.com/2010/04/01/random-sample/
            // Used Swapping instead of Floyd because subPillarSize is large, to avoid hashCode() hit
            Object[] sandboxPillar = basePillar.toArray(); // Clone to avoid changing basePillar
            List<Object> subPillar = new ArrayList<>(subPillarSize);
            for (int i = 0; i < subPillarSize; i++) {
                int index = i + workingRandom.nextInt(basePillarSize - i);
                subPillar.add(sandboxPillar[index]);
                sandboxPillar[index] = sandboxPillar[i];
            }
            return subPillar;
        }

        private List<Object> selectBasePillar() {
            // Known issue/compromise: Every subPillar should have same probability, but doesn't.
            // Instead, every basePillar has the same probability.
            int baseListIndex = workingRandom.nextInt(cachedBasePillarList.size());
            return cachedBasePillarList.get(baseListIndex);
        }

    }

}
