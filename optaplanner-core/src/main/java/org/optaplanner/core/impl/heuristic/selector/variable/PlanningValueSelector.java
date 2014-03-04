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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.IteratorUtils;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.descriptor.EntityIndependentValueRangeDescriptor;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.PlanningVariableDescriptor;
import org.optaplanner.core.impl.phase.scope.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Determines the order in which the planning values of 1 planning entity class are selected for an algorithm
 */
@Deprecated
public class PlanningValueSelector extends SolverPhaseLifecycleListenerAdapter {

    private ValueRangeDescriptor valueRangeDescriptor;

    private PlanningValueSelectionOrder selectionOrder = PlanningValueSelectionOrder.ORIGINAL;
    private PlanningValueSelectionPromotion selectionPromotion = PlanningValueSelectionPromotion.NONE; // TODO
    private boolean roundRobinSelection = false; // TODO

    private ScoreDirector scoreDirector;
    private Random workingRandom;
    private Collection<?> cachedPlanningValues = null;

    public PlanningValueSelector(PlanningVariableDescriptor variableDescriptor) {
        this.valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
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
        scoreDirector = phaseScope.getScoreDirector();
        workingRandom = phaseScope.getWorkingRandom();
        initSelectedPlanningValueList(phaseScope);
    }

    private void initSelectedPlanningValueList(AbstractSolverPhaseScope phaseScope) {
        if (valueRangeDescriptor.isEntityIndependent()) {
            CountableValueRange<?> valueRange = (CountableValueRange<?>)
                    ((EntityIndependentValueRangeDescriptor) valueRangeDescriptor).extractValueRange(
                            phaseScope.getWorkingSolution());
            cachedPlanningValues = IteratorUtils.toList(
                    valueRange.createOriginalIterator(), (int) valueRange.getSize());
        } else {
            cachedPlanningValues = null;
        }
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope phaseScope) {
        cachedPlanningValues = null;
    }

    public Iterator<?> iterator(Object entity) {
        if (cachedPlanningValues != null) {
            return cachedPlanningValues.iterator();
        } else {
            CountableValueRange<?> valueRange = (CountableValueRange<?>)valueRangeDescriptor.extractValueRange(
                    scoreDirector.getWorkingSolution(), entity);
            List<?> values = IteratorUtils.toList(valueRange.createOriginalIterator(), (int) valueRange.getSize());
            return values.iterator();
        }
    }

}
