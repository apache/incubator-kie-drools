package org.drools.planner.core.heuristic.selector.entity;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.entity.PlanningEntitySorter;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.event.SolverPhaseLifecycleListenerAdapter;

/**
 * Determines the order in which the planning entities of 1 planning entity class are selected for an algorithm
 */
public class PlanningEntitySelector extends SolverPhaseLifecycleListenerAdapter
        implements Iterable<Object> {

    private PlanningEntityDescriptor planningEntityDescriptor;

    private PlanningEntitySelectionOrder selectionOrder = PlanningEntitySelectionOrder.ORIGINAL;
    private boolean resetInitializedPlanningEntities = false;

    private List<Object> selectedPlanningEntityList = null;

    public PlanningEntitySelector(PlanningEntityDescriptor planningEntityDescriptor) {
        this.planningEntityDescriptor = planningEntityDescriptor;
    }

    public void setSelectionOrder(PlanningEntitySelectionOrder selectionOrder) {
        this.selectionOrder = selectionOrder;
    }

    public void setResetInitializedPlanningEntities(boolean resetInitializedPlanningEntities) {
        this.resetInitializedPlanningEntities = resetInitializedPlanningEntities;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        validateConfiguration();
        initSelectedPlanningEntityList(solverPhaseScope);
    }

    private void validateConfiguration() {
        if (selectionOrder == PlanningEntitySelectionOrder.DECREASING_DIFFICULTY) {
            PlanningEntitySorter planningEntitySorter = planningEntityDescriptor.getPlanningEntitySorter();
            if (!planningEntitySorter.isSortDifficultySupported()) {
                throw new IllegalStateException("The selectionOrder (" + selectionOrder
                        + ") can not be used on a PlanningEntity ("
                        + planningEntityDescriptor.getPlanningEntityClass().getName()
                        + ") that has no support for difficulty sorting. Check the @PlanningEntity annotation.");
            }
        }
    }

    private void initSelectedPlanningEntityList(AbstractSolverPhaseScope solverPhaseScope) {
        List<Object> workingPlanningEntityList = solverPhaseScope.getWorkingPlanningEntityList();
        for (Iterator<Object> it = workingPlanningEntityList.iterator(); it.hasNext(); ) {
            Object planningEntity = it.next();
            if (!planningEntityDescriptor.getPlanningEntityClass().isInstance(planningEntity)) {
                it.remove();
            } else if (planningEntityDescriptor.isInitialized(planningEntity)) {
                if (resetInitializedPlanningEntities) {
                    WorkingMemory workingMemory = solverPhaseScope.getWorkingMemory();
                    workingMemory.retract(workingMemory.getFactHandle(planningEntity));
                    planningEntityDescriptor.uninitialize(planningEntity);
                } else {
                    // Do not plan the initialized planning entity
                    it.remove();
                }
            }
        }
        switch (selectionOrder) {
            case ORIGINAL:
                break;
            case RANDOM:
                Collections.shuffle(workingPlanningEntityList, solverPhaseScope.getWorkingRandom());
                break;
            case DECREASING_DIFFICULTY:
                PlanningEntitySorter planningEntitySorter = planningEntityDescriptor.getPlanningEntitySorter();
                planningEntitySorter.sortDifficultyDescending(
                        solverPhaseScope.getWorkingSolution(), workingPlanningEntityList);
                break;
            default:
                throw new IllegalStateException("The selectionOrder (" + selectionOrder + ") is not implemented");
        }
        selectedPlanningEntityList = workingPlanningEntityList;
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        selectedPlanningEntityList = null;
    }

    public Iterator<Object> iterator() {
        return selectedPlanningEntityList.iterator();
    }

}
