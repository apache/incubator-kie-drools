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

package org.drools.planner.core.heuristic.selector.move.cached;

import java.util.Collections;

import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.solver.DefaultSolverScope;

public class ShufflingMoveSelector extends CachingMoveSelector {

    public ShufflingMoveSelector(MoveSelector childMoveSelector, SelectionCacheType cacheType) {
        super(childMoveSelector, cacheType);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    protected void orderCache(DefaultSolverScope solverScope) {
        Collections.shuffle(cachedMoveList, solverScope.getWorkingRandom());
    }

    @Override
    public String toString() {
        return "Shuffling(" + childMoveSelector + ")";
    }

}
