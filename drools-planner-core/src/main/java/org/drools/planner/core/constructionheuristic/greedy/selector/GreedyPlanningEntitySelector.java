package org.drools.planner.core.constructionheuristic.greedy.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.drools.planner.core.constructionheuristic.greedy.GreedySolverScope;
import org.drools.planner.core.constructionheuristic.greedy.event.GreedySolverLifecycleListenerAdapter;

/**
 * Determines the order in which the planning entities are fit into the planning
 */
public class GreedyPlanningEntitySelector extends GreedySolverLifecycleListenerAdapter
        implements Iterable<Object> {

    private Comparator<Object> fitOrderPlanningEntityComparator = null;

    private Collection<Object> fitOrderPlanningEntities = null;

    public void setFitOrderPlanningEntityComparator(Comparator<Object> fitOrderPlanningEntityComparator) {
        this.fitOrderPlanningEntityComparator = fitOrderPlanningEntityComparator;
    }

    @Override
    public void solvingStarted(GreedySolverScope greedySolverScope) {
        Collection<Object> planningEntities = greedySolverScope.getWorkingPlanningEntities();
        if (fitOrderPlanningEntityComparator == null) {
            // Return them in the order as defined on the
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
