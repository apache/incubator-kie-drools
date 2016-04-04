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

package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.CachingMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.CachingValueSelector;

/**
 * A {@link EntitySelector} that caches the result of its child {@link EntitySelector}.
 * <p>
 * Keep this code in sync with {@link CachingValueSelector} and {@link CachingMoveSelector}.
 */
public class CachingEntitySelector extends AbstractCachingEntitySelector {

    protected final boolean randomSelection;

    public CachingEntitySelector(EntitySelector childEntitySelector, SelectionCacheType cacheType,
            boolean randomSelection) {
        super(childEntitySelector, cacheType);
        this.randomSelection = randomSelection;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isNeverEnding() {
        // CachedListRandomIterator is neverEnding
        return randomSelection;
    }

    @Override
    public Iterator<Object> iterator() {
        if (!randomSelection) {
            return cachedEntityList.iterator();
        } else {
            return new CachedListRandomIterator<>(cachedEntityList, workingRandom);
        }
    }

    @Override
    public ListIterator<Object> listIterator() {
        if (!randomSelection) {
            return cachedEntityList.listIterator();
        } else {
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        if (!randomSelection) {
            return cachedEntityList.listIterator(index);
        } else {
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    @Override
    public String toString() {
        return "Caching(" + childEntitySelector + ")";
    }

}
