package org.drools.planner.core.heuristic.selector.value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.domain.variable.PlanningValueSorter;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.event.SolverPhaseLifecycleListenerAdapter;

/**
 * Determines the order in which the planning values of 1 planning entity class are selected for an algorithm
 */
public class PlanningValueSelector extends SolverPhaseLifecycleListenerAdapter
        implements Iterable<Object> {

    private PlanningVariableDescriptor planningVariableDescriptor;

    private PlanningValueSelectionOrder selectionOrder = PlanningValueSelectionOrder.ORIGINAL;
    private PlanningValueSelectionPromotion selectionPromotion = PlanningValueSelectionPromotion.NONE;
    private boolean roundRobinSelection = false;

    private List<Object> selectedPlanningValueList = null;

    public PlanningValueSelector(PlanningVariableDescriptor planningVariableDescriptor) {
        this.planningVariableDescriptor = planningVariableDescriptor;
    }

    public void setSelectionOrder(PlanningValueSelectionOrder selectionOrder) {
        this.selectionOrder = selectionOrder;
    }

    public void setSelectionPromotion(PlanningValueSelectionPromotion selectionPromotion) {
        this.selectionPromotion = selectionPromotion;
    }

    public void setRoundRobinSelection(boolean roundRobinSelection) {
        this.roundRobinSelection = roundRobinSelection;
    }

    @Override
    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        validateConfiguration();
        initSelectedPlanningValueList(solverPhaseScope);
    }

    private void validateConfiguration() {
        if (selectionOrder == PlanningValueSelectionOrder.INCREASING_STRENGTH) {
            PlanningValueSorter planningValueSorter = planningVariableDescriptor.getPlanningValueSorter();
            if (!planningValueSorter.isSortStrengthSupported()) {
                throw new IllegalStateException("The selectionOrder (" + selectionOrder
                        + ") can not be used on PlanningEntity ("
                        + planningVariableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass().getName()
                        + ")'s planningVariable (" + planningVariableDescriptor.getVariablePropertyName()
                        + ") that has no support for strength sorting. Check the @PlanningVariable annotation.");
            }
        }
    }

    private void initSelectedPlanningValueList(AbstractSolverPhaseScope solverPhaseScope) {
        List<Object> workingPlanningValueList = planningVariableDescriptor.getPlanningValueList(
                solverPhaseScope.getWorkingSolution());
        switch (selectionOrder) {
            case ORIGINAL:
                break;
            case RANDOM:
                Collections.shuffle(workingPlanningValueList, solverPhaseScope.getWorkingRandom());
                break;
            case INCREASING_STRENGTH:
                PlanningValueSorter planningValueSorter = planningVariableDescriptor.getPlanningValueSorter();
                planningValueSorter.sortStrengthAscending(
                        solverPhaseScope.getWorkingSolution(), workingPlanningValueList);
                break;
            default:
                throw new IllegalStateException("The selectionOrder (" + selectionOrder + ") is not implemented");
        }
        selectedPlanningValueList = workingPlanningValueList;
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        selectedPlanningValueList = null;
    }

    public Iterator<Object> iterator() {
        return selectedPlanningValueList.iterator();
    }

}
