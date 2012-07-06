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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.cached.SelectionFilter;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.entity.cached.CachingEntitySelector;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolverScope;

public class CachingFilteringMoveSelector extends CachingMoveSelector {

    protected final SelectionFilter moveFilter;

    public CachingFilteringMoveSelector(MoveSelector childMoveSelector, SelectionCacheType cacheType,
            SelectionFilter moveFilter) {
        super(childMoveSelector, cacheType);
        this.moveFilter = moveFilter;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void constructCache(DefaultSolverScope solverScope) {
        ScoreDirector scoreDirector = solverScope.getScoreDirector();
        long childSize = childMoveSelector.getSize();
        cachedMoveList = new ArrayList<Move>((int) childSize);
        for (Move move : childMoveSelector) {
            if (moveFilter.accept(scoreDirector, move)) {
                cachedMoveList.add(move);
            }
        }
    }

    @Override
    public String toString() {
        return "Filtering(" + childMoveSelector + ")";
    }

}
