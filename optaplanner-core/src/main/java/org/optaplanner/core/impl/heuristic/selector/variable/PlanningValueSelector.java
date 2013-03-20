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

package org.optaplanner.core.impl.heuristic.selector.variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.optaplanner.core.impl.domain.variable.PlanningValueSorter;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Determines the order in which the planning values of 1 planning entity class are selected for an algorithm
 */
@Deprecated
public class PlanningValueSelector extends SolverPhaseLifecycleListenerAdapter {

    private PlanningVariableDescriptor planningVariableDescriptor;

    private PlanningValueSelectionOrder selectionOrder = PlanningValueSelectionOrder.ORIGINAL;
    private PlanningValueSelectionPromotion selectionPromotion = PlanningValueSelectionPromotion.NONE; // TODO
    private boolean roundRobinSelection = false; // TODO

    private ScoreDirector scoreDirector;
    private Random workingRandom;
    private Collection<?> cachedPlanningValues = null;

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

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractSolverPhaseScope phaseScope) {
        validate();
        scoreDirector = phaseScope.getScoreDirector();
        workingRandom = phaseScope.getWorkingRandom();
        initSelectedPlanningValueList(phaseScope);
    }

    private void validate() {
        if (selectionOrder == PlanningValueSelectionOrder.INCREASING_STRENGTH) {
            PlanningValueSorter valueSorter = planningVariableDescriptor.getValueSorter();
            if (!valueSorter.isSortStrengthSupported()) {
                throw new IllegalStateException("The selectionOrder (" + selectionOrder
                        + ") can not be used on PlanningEntity ("
                        + planningVariableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass().getName()
                        + ")'s planningVariable (" + planningVariableDescriptor.getVariableName()
                        + ") that has no support for strength sorting. Check the @PlanningVariable annotation.");
            }
        }
    }

    private void initSelectedPlanningValueList(AbstractSolverPhaseScope phaseScope) {
        if (planningVariableDescriptor.isPlanningValuesCacheable()) {
            Collection<?> planningValues = planningVariableDescriptor.extractPlanningValues(
                    phaseScope.getWorkingSolution(), null);
            cachedPlanningValues = applySelectionOrder(planningValues);
        } else {
            cachedPlanningValues = null;
        }
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope phaseScope) {
        cachedPlanningValues = null;
    }

    public Iterator<?> iterator(Object planningEntity) {
        if (cachedPlanningValues != null) {
            return cachedPlanningValues.iterator();
        } else {
            Collection<?> planningValues = planningVariableDescriptor.extractPlanningValues(
                    scoreDirector.getWorkingSolution(), planningEntity);
            planningValues = applySelectionOrder(planningValues);
            return planningValues.iterator();
        }
    }

    private Collection<?> applySelectionOrder(Collection<?> workingPlanningValues) {
        switch (selectionOrder) {
            case ORIGINAL:
                return workingPlanningValues;
            case RANDOM:
                List<Object> randomPlanningValueList = new ArrayList<Object>(workingPlanningValues);
                Collections.shuffle(randomPlanningValueList, workingRandom);
                return randomPlanningValueList;
            case INCREASING_STRENGTH:
                List<Object> increasingStrengthPlanningValueList = new ArrayList<Object>(workingPlanningValues);
                PlanningValueSorter valueSorter = planningVariableDescriptor.getValueSorter();
                valueSorter.sortStrengthAscending(
                        scoreDirector.getWorkingSolution(), increasingStrengthPlanningValueList);
                return increasingStrengthPlanningValueList;
            default:
                throw new IllegalStateException("The selectionOrder (" + selectionOrder + ") is not implemented.");
        }
    }

}
