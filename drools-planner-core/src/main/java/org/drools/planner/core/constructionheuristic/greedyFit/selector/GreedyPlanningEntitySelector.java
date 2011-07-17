package org.drools.planner.core.constructionheuristic.greedyFit.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.IteratorChain;
import org.drools.WorkingMemory;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitSolverPhaseScope;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitStepScope;
import org.drools.planner.core.constructionheuristic.greedyFit.event.GreedySolverPhaseLifecycleListener;
import org.drools.planner.core.constructionheuristic.greedyFit.event.GreedySolverPhaseLifecycleListenerAdapter;
import org.drools.planner.core.domain.entity.PlanningEntitySorter;
import org.drools.planner.core.domain.meta.PlanningEntityDescriptor;
import org.drools.planner.core.heuristic.selector.PlanningEntitySelector;

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

    public void beforeDeciding(GreedyFitStepScope greedyFitStepScope) {
        for (PlanningEntitySelector planningEntitySelector : planningEntitySelectorList) {
            planningEntitySelector.beforeDeciding(greedyFitStepScope);
        }
    }

    public void stepDecided(GreedyFitStepScope greedyFitStepScope) {
        for (PlanningEntitySelector planningEntitySelector : planningEntitySelectorList) {
            planningEntitySelector.stepDecided(greedyFitStepScope);
        }
    }

    public void stepTaken(GreedyFitStepScope greedyFitStepScope) {
        for (PlanningEntitySelector planningEntitySelector : planningEntitySelectorList) {
            planningEntitySelector.stepTaken(greedyFitStepScope);
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
