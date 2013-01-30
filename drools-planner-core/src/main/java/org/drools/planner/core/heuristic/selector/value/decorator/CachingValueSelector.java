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

package org.drools.planner.core.heuristic.selector.value.decorator;

import java.util.Iterator;

import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.drools.planner.core.heuristic.selector.entity.decorator.CachingEntitySelector;
import org.drools.planner.core.heuristic.selector.move.decorator.CachingMoveSelector;
import org.drools.planner.core.heuristic.selector.value.EntityIndependentValueSelector;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;

/**
 * A {@link ValueSelector} that caches the result of its child {@link ValueSelector}.
 * <p/>
 * Keep this code in sync with {@link CachingEntitySelector} and {@link CachingMoveSelector}.
 */
public class CachingValueSelector extends AbstractCachingValueSelector implements EntityIndependentValueSelector {

    protected final boolean randomSelection;

    public CachingValueSelector(EntityIndependentValueSelector childValueSelector, SelectionCacheType cacheType,
            boolean randomSelection) {
        super(childValueSelector, cacheType);
        this.randomSelection = randomSelection;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isNeverEnding() {
        // CachedListRandomIterator is neverEnding
        return randomSelection;
    }

    public Iterator<Object> iterator(Object entity) {
        return iterator();
    }

    public Iterator<Object> iterator() {
        if (!randomSelection) {
            return cachedValueList.iterator();
        } else {
            return new CachedListRandomIterator<Object>(cachedValueList, workingRandom);
        }
    }

    @Override
    public String toString() {
        return "Caching(" + childValueSelector + ")";
    }

}
