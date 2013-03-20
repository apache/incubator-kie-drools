/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.constructionheuristic.greedyFit.selector;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.IteratorChain;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.event.GreedySolverPhaseLifecycleListener;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.scope.GreedyFitSolverPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.scope.GreedyFitStepScope;
import org.optaplanner.core.impl.heuristic.selector.entity.PlanningEntitySelector;

/**
 * Determines the order in which the planning entities are fit into the solution
 */
public class GreedyPlanningEntitySelector implements Iterable<Object>, GreedySolverPhaseLifecycleListener {

    private List<PlanningEntitySelector> planningEntitySelectorList;

    public void setPlanningEntitySelectorList(List<PlanningEntitySelector> planningEntitySelectorList) {
        this.planningEntitySelectorList = planningEntitySelectorList;
    }

    public void phaseStarted(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope) {
        for (PlanningEntitySelector planningEntitySelector : planningEntitySelectorList) {
            planningEntitySelector.phaseStarted(greedyFitSolverPhaseScope);
        }
    }

    public void stepStarted(GreedyFitStepScope greedyFitStepScope) {
        for (PlanningEntitySelector planningEntitySelector : planningEntitySelectorList) {
            planningEntitySelector.stepStarted(greedyFitStepScope);
        }
    }

    public void stepEnded(GreedyFitStepScope greedyFitStepScope) {
        for (PlanningEntitySelector planningEntitySelector : planningEntitySelectorList) {
            planningEntitySelector.stepEnded(greedyFitStepScope);
        }
    }

    public void phaseEnded(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope) {
        for (PlanningEntitySelector planningEntitySelector : planningEntitySelectorList) {
            planningEntitySelector.phaseEnded(greedyFitSolverPhaseScope);
        }
    }

    public Iterator<Object> iterator() {
        IteratorChain iteratorChain = new IteratorChain();
        for (PlanningEntitySelector planningEntitySelector : planningEntitySelectorList) {
            iteratorChain.addIterator(planningEntitySelector.iterator());
        }
        return iteratorChain;
    }

}
