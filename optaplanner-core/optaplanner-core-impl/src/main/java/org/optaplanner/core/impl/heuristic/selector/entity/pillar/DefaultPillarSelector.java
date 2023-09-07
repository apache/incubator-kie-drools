/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.entity.pillar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
import org.optaplanner.core.impl.heuristic.selector.move.generic.PillarDemand;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * @see PillarSelector
 */
public final class DefaultPillarSelector<Solution_> extends AbstractSelector<Solution_>
        implements PillarSelector<Solution_>, SelectionCacheLifecycleListener<Solution_> {

    private static final SelectionCacheType CACHE_TYPE = SelectionCacheType.STEP;

    private final EntitySelector<Solution_> entitySelector;
    private final boolean randomSelection;
    private final SubPillarConfigPolicy subpillarConfigPolicy;
    private final PillarDemand<Solution_> pillarDemand;

    private List<List<Object>> cachedBasePillarList = null;

    public DefaultPillarSelector(EntitySelector<Solution_> entitySelector,
            List<GenuineVariableDescriptor<Solution_>> variableDescriptors, boolean randomSelection,
            SubPillarConfigPolicy subpillarConfigPolicy) {
        this.entitySelector = entitySelector;
        this.randomSelection = randomSelection;
        this.subpillarConfigPolicy = subpillarConfigPolicy;
        this.pillarDemand = new PillarDemand<>(entitySelector, variableDescriptors, subpillarConfigPolicy);
        Class<?> entityClass = entitySelector.getEntityDescriptor().getEntityClass();
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptors) {
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
        if (entitySelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has an entitySelector (" + entitySelector
                    + ") with neverEnding (" + entitySelector.isNeverEnding() + ").");
        }
        phaseLifecycleSupport.addEventListener(entitySelector);
        phaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge<>(CACHE_TYPE, this));
        boolean subPillarEnabled = subpillarConfigPolicy.isSubPillarEnabled();
        if (!randomSelection && subPillarEnabled) {
            throw new IllegalStateException("The selector (" + this
                    + ") with randomSelection  (" + randomSelection + ") does not support non random selection with "
                    + "sub pillars because the number of sub pillars scales exponentially.\n"
                    + "Either set subPillarType to " + SubPillarType.NONE + " or use JIT random selection.");
        }
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    @Override
    public EntityDescriptor<Solution_> getEntityDescriptor() {
        return entitySelector.getEntityDescriptor();
    }

    @Override
    public SelectionCacheType getCacheType() {
        return CACHE_TYPE;
    }

    PillarDemand<Solution_> getPillarDemand() {
        return pillarDemand;
    }

    @Override
    public void constructCache(SolverScope<Solution_> solverScope) {
        /*
         * The first pillar selector creates the supply.
         * Other matching pillar selectors, if there are any, reuse the supply.
         */
        cachedBasePillarList = solverScope.getScoreDirector().getSupplyManager()
                .demand(pillarDemand)
                .read();
    }

    @Override
    public void disposeCache(SolverScope<Solution_> solverScope) {
        /*
         * Cancel the demand of each pillar selector.
         * The final pillar selector's demand cancellation will cause the supply to be removed entirely.
         */
        solverScope.getScoreDirector().getSupplyManager()
                .cancel(pillarDemand);
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
