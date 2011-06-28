package org.drools.planner.core.constructionheuristic.greedy.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.drools.planner.core.constructionheuristic.greedy.GreedySolverPhaseScope;
import org.drools.planner.core.constructionheuristic.greedy.event.GreedySolverPhaseLifecycleListenerAdapter;
import org.drools.planner.core.domain.entity.PlanningEntityDifficultyWeightFactory;
import org.drools.planner.core.domain.entity.PlanningEntityDifficultyWeightUtils;

/**
 * Determines the order in which the planning entities are fit into the planning
 */
public class GreedyPlanningEntitySelector extends GreedySolverPhaseLifecycleListenerAdapter
        implements Iterable<Object> {

    private Comparator<Object> fitOrderPlanningEntityComparator = null;
    private PlanningEntityDifficultyWeightFactory planningEntityDifficultyWeightFactory = null;

    private Collection<Object> fitOrderPlanningEntities = null;

    public void setFitOrderPlanningEntityComparator(Comparator<Object> fitOrderPlanningEntityComparator) {
        this.fitOrderPlanningEntityComparator = fitOrderPlanningEntityComparator;
    }

    public void setPlanningEntityDifficultyWeightFactory(
            PlanningEntityDifficultyWeightFactory planningEntityDifficultyWeightFactory) {
        this.planningEntityDifficultyWeightFactory = planningEntityDifficultyWeightFactory;
    }

    @Override
    public void phaseStarted(GreedySolverPhaseScope greedySolverPhaseScope) {
        validateConfiguration();
        initFitOrderPlanningEntities(greedySolverPhaseScope);
    }

    private void validateConfiguration() {
        if (fitOrderPlanningEntityComparator != null && planningEntityDifficultyWeightFactory != null) {
            throw new IllegalArgumentException("The fitOrderPlanningEntityComparator and " +
                    "planningEntityDifficultyWeightFactory cannot be applied together.");
        }
    }

    private void initFitOrderPlanningEntities(GreedySolverPhaseScope greedySolverPhaseScope) {
        Collection<Object> planningEntities = greedySolverPhaseScope.getWorkingPlanningEntities();
        if (fitOrderPlanningEntityComparator != null) {
            List<Object> fitOrderPlanningEntityList = new ArrayList<Object>(planningEntities);
            Collections.sort(fitOrderPlanningEntityList, fitOrderPlanningEntityComparator);
            fitOrderPlanningEntities = fitOrderPlanningEntityList;
        } else if (planningEntityDifficultyWeightFactory != null) {
            fitOrderPlanningEntities = PlanningEntityDifficultyWeightUtils.sortByDifficultyWeight(
                    greedySolverPhaseScope.getWorkingSolution(), planningEntities,
                    planningEntityDifficultyWeightFactory, false);
        } else {
            // Return them in the order as defined on the solution
            fitOrderPlanningEntities = planningEntities;
        }
    }

    public Iterator<Object> iterator() {
        return fitOrderPlanningEntities.iterator();
    }

}
