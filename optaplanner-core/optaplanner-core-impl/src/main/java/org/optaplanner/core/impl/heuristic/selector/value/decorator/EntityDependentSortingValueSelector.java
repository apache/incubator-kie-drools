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

package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractDemandEnabledSelector;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public final class EntityDependentSortingValueSelector<Solution_>
        extends AbstractDemandEnabledSelector<Solution_>
        implements ValueSelector<Solution_> {

    private final ValueSelector<Solution_> childValueSelector;
    private final SelectionCacheType cacheType;
    private final SelectionSorter<Solution_, Object> sorter;

    protected ScoreDirector<Solution_> scoreDirector = null;

    public EntityDependentSortingValueSelector(ValueSelector<Solution_> childValueSelector,
            SelectionCacheType cacheType, SelectionSorter<Solution_, Object> sorter) {
        this.childValueSelector = childValueSelector;
        this.cacheType = cacheType;
        this.sorter = sorter;
        if (childValueSelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with neverEnding (" + childValueSelector.isNeverEnding() + ").");
        }
        if (cacheType != SelectionCacheType.STEP) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        phaseLifecycleSupport.addEventListener(childValueSelector);
    }

    public ValueSelector<Solution_> getChildValueSelector() {
        return childValueSelector;
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        scoreDirector = phaseScope.getScoreDirector();
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        scoreDirector = null;
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    @Override
    public long getSize(Object entity) {
        return childValueSelector.getSize(entity);
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        long childSize = childValueSelector.getSize(entity);
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with childSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        List<Object> cachedValueList = new ArrayList<>((int) childSize);
        childValueSelector.iterator(entity).forEachRemaining(cachedValueList::add);
        logger.trace("    Created cachedValueList: size ({}), valueSelector ({}).",
                cachedValueList.size(), this);
        sorter.sort(scoreDirector, cachedValueList);
        logger.trace("    Sorted cachedValueList: size ({}), valueSelector ({}).",
                cachedValueList.size(), this);
        return cachedValueList.iterator();
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        return iterator(entity);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        EntityDependentSortingValueSelector<?> that = (EntityDependentSortingValueSelector<?>) other;
        return Objects.equals(childValueSelector, that.childValueSelector) && cacheType == that.cacheType
                && Objects.equals(sorter, that.sorter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childValueSelector, cacheType, sorter);
    }

    @Override
    public String toString() {
        return "Sorting(" + childValueSelector + ")";
    }

}
