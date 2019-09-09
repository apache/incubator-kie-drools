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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.entity.pillar.SubPillarConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.move.generic.SubPillarType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelector;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * @see PillarSelector
 */
public class DefaultPillarSelector extends AbstractSelector implements PillarSelector,
        SelectionCacheLifecycleListener {

    protected static final SelectionCacheType CACHE_TYPE = SelectionCacheType.STEP;

    protected final EntitySelector entitySelector;
    protected final List<GenuineVariableDescriptor> variableDescriptors;
    protected final boolean randomSelection;
    protected final SubPillarConfigPolicy subpillarConfigPolicy;

    protected List<List<Object>> cachedBasePillarList = null;

    public DefaultPillarSelector(EntitySelector entitySelector, List<GenuineVariableDescriptor> variableDescriptors,
            boolean randomSelection, SubPillarConfigPolicy subpillarConfigPolicy) {
        this.entitySelector = entitySelector;
        this.variableDescriptors = variableDescriptors;
        this.randomSelection = randomSelection;
        this.subpillarConfigPolicy = subpillarConfigPolicy;
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
        boolean subPillarEnabled = subpillarConfigPolicy.isSubPillarEnabled();
        if (!randomSelection && subPillarEnabled) {
            throw new IllegalStateException("The selector (" + this
                    + ") with randomSelection  (" + randomSelection + ") does not support non random selection with "
                    + "sub pillars because the number of sub pillars scales exponentially.\n"
                    + "Either set subPillarType to " + SubPillarType.NONE + " or use JIT random selection.");
        }
    }

    private static List<Object> getSingleVariableValueState(Object entity,
            List<GenuineVariableDescriptor> variableDescriptors) {
        Object value = variableDescriptors.get(0).getValue(entity);
        return Collections.singletonList(value);
    }

    private static List<Object> getMultiVariableValueState(Object entity,
            List<GenuineVariableDescriptor> variableDescriptors, int variableCount) {
        List<Object> valueState = new ArrayList<>(variableCount);
        for (int i = 0; i < variableCount; i++) {
            Object value = variableDescriptors.get(i).getValue(entity);
            valueState.add(value);
        }
        return valueState;
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    @Override
    public EntityDescriptor getEntityDescriptor() {
        return entitySelector.getEntityDescriptor();
    }

    @Override
    public SelectionCacheType getCacheType() {
        return CACHE_TYPE;
    }

    @Override
    public void constructCache(DefaultSolverScope solverScope) {
        long entitySize = entitySelector.getSize();
        if (entitySize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this + ") has an entitySelector ("
                    + entitySelector + ") with entitySize (" + entitySize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        Stream<Object> entities = StreamSupport.stream(entitySelector.spliterator(), false);
        Comparator<?> comparator = subpillarConfigPolicy.getEntityComparator();
        if (comparator != null) {
            /*
             * The entity selection will be sorted. This will result in all the pillars being sorted without having to
             * sort them individually later.
             */
            entities = entities.sorted((Comparator) comparator);
        }
        // Create all the pillars from a stream of entities; if sorted, the pillars will be sequential.
        Map<List<Object>, List<Object>> valueStateToPillarMap = new LinkedHashMap<>((int) entitySize);
        int variableCount = variableDescriptors.size();
        entities.forEach(entity -> {
            List<Object> valueState = variableCount == 1 ?
                    getSingleVariableValueState(entity, variableDescriptors) :
                    getMultiVariableValueState(entity, variableDescriptors, variableCount);
            List<Object> pillar = valueStateToPillarMap.computeIfAbsent(valueState, key -> new ArrayList<>());
            pillar.add(entity);
        });
        // Store the cache. Exclude pillars of size lower than the minimumSubPillarSize, as we shouldn't select those.
        Collection<List<Object>> pillarLists = valueStateToPillarMap.values();
        int minimumSubPillarSize = subpillarConfigPolicy.getMinimumSubPillarSize();
        if (minimumSubPillarSize > 1) {
            cachedBasePillarList = pillarLists.stream()
                    .filter(pillar -> pillar.size() >= minimumSubPillarSize)
                    .collect(Collectors.toList());
        } else { // Use shortcut when we don't intend to remove anything.
            cachedBasePillarList = new ArrayList<>(pillarLists);
        }
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
        if (!subpillarConfigPolicy.isSubPillarEnabled()) {
            return cachedBasePillarList.size();
        } else {
            // For each pillar, the number of combinations is: the sum of every (n! / (k! (n-k)!)) for which n is
            // pillar.getSize() and k iterates from minimumSubPillarSize to maximumSubPillarSize. This implies that a
            // single pillar of size 64 is already too big to be held in a long.
            throw new UnsupportedOperationException("The selector (" + this
                    + ") with randomSelection  (" + randomSelection + ") and sub pillars does not support getSize() "
                    + "because the number of sub pillars scales exponentially.");
        }
    }

    @Override
    public Iterator<List<Object>> iterator() {
        boolean subPillarEnabled = subpillarConfigPolicy.isSubPillarEnabled();
        if (!randomSelection) {
            if (!subPillarEnabled) {
                return cachedBasePillarList.iterator();
            } else {
                throw new IllegalStateException(getSubPillarExceptionMessage());
            }
        } else {
            if (!subPillarEnabled) {
                return new CachedListRandomIterator<>(cachedBasePillarList, workingRandom);
            } else {
                return new RandomSubPillarIterator();
            }
        }
    }

    private String getSubPillarExceptionMessage() {
        return "Impossible state because the constructors fails with randomSelection (" + randomSelection
                + ") and sub pillars.";
    }

    private String getListIteratorExceptionMessage() {
        return "The selector (" + this + ") does not support a ListIterator with randomSelection (" + randomSelection
                + ").";
    }

    @Override
    public ListIterator<List<Object>> listIterator() {
        boolean subPillarEnabled = subpillarConfigPolicy.isSubPillarEnabled();
        if (!randomSelection) {
            if (!subPillarEnabled) {
                return cachedBasePillarList.listIterator();
            } else {
                throw new IllegalStateException(getSubPillarExceptionMessage());
            }
        } else {
            throw new IllegalStateException(getListIteratorExceptionMessage());
        }
    }

    @Override
    public ListIterator<List<Object>> listIterator(int index) {
        boolean subPillarEnabled = subpillarConfigPolicy.isSubPillarEnabled();
        if (!randomSelection) {
            if (!subPillarEnabled) {
                return cachedBasePillarList.listIterator(index);
            } else {
                throw new IllegalStateException(getSubPillarExceptionMessage());
            }
        } else {
            throw new IllegalStateException(getListIteratorExceptionMessage());
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
            int basePillarSize = basePillar.size();
            if (basePillarSize == 1) { // no subpillar to select
                return basePillar;
            }
            // Known issue/compromise: Every subPillar should have same probability, but doesn't.
            // Instead, every subPillar size has the same probability.
            int min = Math.min(subpillarConfigPolicy.getMinimumSubPillarSize(), basePillarSize);
            int max = Math.min(subpillarConfigPolicy.getMaximumSubPillarSize(), basePillarSize);
            int subPillarSize = min + workingRandom.nextInt(max - min + 1);
            if (subPillarSize == basePillarSize) { // subpillar is equal to the base pillar, use shortcut
                return basePillar;
            } else if (subPillarSize == 1) { // subpillar is just one element, use shortcut
                final int randomIndex = workingRandom.nextInt(basePillarSize);
                final Object randomElement = basePillar.get(randomIndex);
                return Collections.singletonList(randomElement);
            }
            Comparator<?> comparator = subpillarConfigPolicy.getEntityComparator();
            if (comparator == null) {
                return selectRandom(basePillar, subPillarSize);
            } else { // sequential subpillars
                return selectSublist(basePillar, subPillarSize);
            }
        }

        private List<Object> selectSublist(final List<Object> basePillar, final int subPillarSize) {
            final int randomStartingIndex = workingRandom.nextInt(basePillar.size() - subPillarSize);
            return basePillar.subList(randomStartingIndex, randomStartingIndex + subPillarSize);
        }

        private List<Object> selectRandom(final List<Object> basePillar, final int subPillarSize) {
            // Random sampling: See http://eyalsch.wordpress.com/2010/04/01/random-sample/
            // Used Swapping instead of Floyd because subPillarSize is large, to avoid hashCode() hit
            Object[] sandboxPillar = basePillar.toArray(); // Clone to avoid changing basePillar
            List<Object> subPillar = new ArrayList<>(subPillarSize);
            for (int i = 0; i < subPillarSize; i++) {
                int index = i + workingRandom.nextInt(basePillar.size() - i);
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
