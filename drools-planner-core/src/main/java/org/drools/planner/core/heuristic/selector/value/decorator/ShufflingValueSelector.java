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

import java.util.Collections;

import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.entity.decorator.CachingEntitySelector;
import org.drools.planner.core.heuristic.selector.move.decorator.CachingMoveSelector;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.heuristic.selector.value.iterator.IteratorToValueIteratorBridge;
import org.drools.planner.core.heuristic.selector.value.iterator.ValueIterator;
import org.drools.planner.core.phase.step.AbstractStepScope;

public class ShufflingValueSelector extends AbstractCachingValueSelector {

    public ShufflingValueSelector(ValueSelector childValueSelector, SelectionCacheType cacheType) {
        super(childValueSelector, cacheType);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isNeverEnding() {
        return false;
    }

    public ValueIterator iterator() {
        Collections.shuffle(cachedValueList, workingRandom);
        return new IteratorToValueIteratorBridge(cachedValueList.iterator());
    }

    @Override
    public String toString() {
        return "Shuffling(" + childValueSelector + ")";
    }

}
