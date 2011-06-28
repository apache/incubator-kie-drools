package org.drools.planner.core.constructionheuristic.greedy.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.drools.planner.core.constructionheuristic.greedy.GreedySolverPhaseScope;
import org.drools.planner.core.constructionheuristic.greedy.event.GreedySolverPhaseLifecycleListenerAdapter;

/**
 * Determines the order in which the planning entities are fit into the planning
 */
public class GreedyPlanningEntitySelector extends GreedySolverPhaseLifecycleListenerAdapter
        implements Iterable<Object> {

    private Comparator<Object> fitOrderPlanningEntityComparator = null;

    private Collection<Object> fitOrderPlanningEntities = null;

    public void setFitOrderPlanningEntityComparator(Comparator<Object> fitOrderPlanningEntityComparator) {
        this.fitOrderPlanningEntityComparator = fitOrderPlanningEntityComparator;
    }

    @Override
    public void phaseStarted(GreedySolverPhaseScope greedySolverPhaseScope) {
        Collection<Object> planningEntities = greedySolverPhaseScope.getWorkingPlanningEntities();
        if (fitOrderPlanningEntityComparator == null) {
            // Return them in the order as defined on the solution
            fitOrderPlanningEntities = planningEntities;
        } else {
            List<Object> fitOrderPlanningEntityList = new ArrayList<Object>(planningEntities);
            Collections.sort(fitOrderPlanningEntityList, fitOrderPlanningEntityComparator);
            fitOrderPlanningEntities = fitOrderPlanningEntityList;
        }
    }

    public Iterator<Object> iterator() {
        return fitOrderPlanningEntities.iterator();
    }

}
