package org.drools.planner.core.constructionheuristic.greedyFit.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedySolverPhaseScope;
import org.drools.planner.core.constructionheuristic.greedyFit.event.GreedySolverPhaseLifecycleListenerAdapter;
import org.drools.planner.core.domain.entity.PlanningEntityDifficultyWeightFactory;
import org.drools.planner.core.domain.entity.PlanningEntityDifficultyWeightUtils;
import org.drools.planner.core.domain.meta.PlanningEntityDescriptor;

/**
 * Determines the order in which the planning entities are fit into the planning
 */
public class GreedyPlanningEntitySelector extends GreedySolverPhaseLifecycleListenerAdapter
        implements Iterable<Object> {

    private boolean resetInitializedPlanningEntities = false;
    private Comparator<Object> fitOrderPlanningEntityComparator = null;
    private PlanningEntityDifficultyWeightFactory planningEntityDifficultyWeightFactory = null;

    private Collection<Object> fitOrderPlanningEntities = null;

    public void setResetInitializedPlanningEntities(boolean resetInitializedPlanningEntities) {
        this.resetInitializedPlanningEntities = resetInitializedPlanningEntities;
    }

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
        for (Iterator<Object> it = planningEntities.iterator(); it.hasNext(); ) {
            Object planningEntity = it.next();
            PlanningEntityDescriptor planningEntityDescriptor = greedySolverPhaseScope.getSolutionDescriptor()
                    .getPlanningEntityDescriptor(planningEntity.getClass());
            if (planningEntityDescriptor.isInitialized(planningEntity)) {
                if (resetInitializedPlanningEntities) {
                    WorkingMemory workingMemory = greedySolverPhaseScope.getWorkingMemory();
                    workingMemory.retract(workingMemory.getFactHandle(planningEntity));
                    planningEntityDescriptor.uninitialize(planningEntity);
                } else {
                    // Do not plan the initialized planning entity
                    it.remove();
                }
            }
        }
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
