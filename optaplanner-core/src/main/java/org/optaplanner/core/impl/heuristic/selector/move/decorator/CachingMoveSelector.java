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

package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import java.util.Iterator;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.CachingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.CachingValueSelector;

/**
 * A {@link MoveSelector} that caches the result of its child {@link MoveSelector}.
 * <p>
 * Keep this code in sync with {@link CachingEntitySelector} and {@link CachingValueSelector}.
 */
public class CachingMoveSelector extends AbstractCachingMoveSelector {

    protected final boolean randomSelection;

    public CachingMoveSelector(MoveSelector childMoveSelector, SelectionCacheType cacheType, boolean randomSelection) {
        super(childMoveSelector, cacheType);
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
    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return cachedMoveList.iterator();
        } else {
            return new CachedListRandomIterator<>(cachedMoveList, workingRandom);
        }
    }

    @Override
    public String toString() {
        return "Caching(" + childMoveSelector + ")";
    }

}
