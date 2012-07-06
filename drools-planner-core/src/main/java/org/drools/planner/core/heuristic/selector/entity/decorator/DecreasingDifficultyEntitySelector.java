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

package org.drools.planner.core.heuristic.selector.entity.decorator;

import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.drools.planner.core.domain.entity.PlanningEntitySorter;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.solver.DefaultSolverScope;

// TODO Refactor to general purpose SortingEntitySelector
public class DecreasingDifficultyEntitySelector extends CachingEntitySelector {

    protected final PlanningEntitySorter planningEntitySorter;

    public DecreasingDifficultyEntitySelector(EntitySelector childEntitySelector, SelectionCacheType cacheType,
            PlanningEntitySorter planningEntitySorter) {
        super(childEntitySelector, cacheType);
        this.planningEntitySorter = planningEntitySorter;
        if (!planningEntitySorter.isSortDifficultySupported()) {
            throw new IllegalStateException("Decreasing difficulty entity selection" +
                    " can not be used on a childEntitySelector ("
                    + childEntitySelector
                    + ") which entity has no support for difficulty sorting. Check the @PlanningEntity annotation.");
            // TODO Create a clearer fail-fast message:
//            throw new IllegalStateException("Decreasing difficulty entity selection" +
//                    " can not be used on a PlanningEntity ("
//                    + planningEntityDescriptor.getPlanningEntityClass().getName()
//                    + ") that has no support for difficulty sorting. Check the @PlanningEntity annotation.");
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void constructCache(DefaultSolverScope solverScope) {
        long childSize = childEntitySelector.getSize();
        cachedEntityList = new ArrayList<Object>((int) childSize);
        CollectionUtils.addAll(cachedEntityList, childEntitySelector.iterator());
        planningEntitySorter.sortDifficultyDescending(solverScope.getWorkingSolution(), cachedEntityList);
    }

    @Override
    public String toString() {
        return "Sorting(" + childEntitySelector + ")";
    }

}
