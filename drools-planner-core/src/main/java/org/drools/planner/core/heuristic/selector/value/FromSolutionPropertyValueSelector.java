/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.heuristic.selector.value;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * This is the common {@link ValueSelector} implementation.
 */
public class FromSolutionPropertyValueSelector extends AbstractValueSelector {

    protected final PlanningVariableDescriptor variableDescriptor;
    protected final boolean randomSelection;
    protected final SelectionCacheType cacheType;

    protected Random workingRandom = null;

    protected List<Object> cachedValueList = null;

    public FromSolutionPropertyValueSelector(PlanningVariableDescriptor variableDescriptor, boolean randomSelection,
            SelectionCacheType cacheType) {
        this.variableDescriptor = variableDescriptor;
        this.randomSelection = randomSelection;
        this.cacheType = cacheType;
        if (cacheType != SelectionCacheType.SOLVER && cacheType != SelectionCacheType.PHASE
                && cacheType != SelectionCacheType.STEP) {
            throw new IllegalArgumentException("The cacheType (" + cacheType
                    + ") is not supported on the class (" + getClass().getName() + ").");
        }
    }

    public PlanningVariableDescriptor getVariableDescriptor() {
        return variableDescriptor;
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        if (cacheType == SelectionCacheType.SOLVER) {
            constructCache(solverScope);
        }
    }

    @Override
    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseStarted(solverPhaseScope);
        if (cacheType == SelectionCacheType.PHASE) {
            constructCache(solverPhaseScope.getSolverScope());
        }
        workingRandom = solverPhaseScope.getWorkingRandom();
    }

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
        super.stepStarted(stepScope);
        if (cacheType == SelectionCacheType.STEP) {
            constructCache(stepScope.getSolverPhaseScope().getSolverScope());
        }
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        super.stepEnded(stepScope);
        if (cacheType == SelectionCacheType.STEP) {
            disposeCache(stepScope.getSolverPhaseScope().getSolverScope());
        }
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseEnded(solverPhaseScope);
        if (cacheType == SelectionCacheType.PHASE) {
            disposeCache(solverPhaseScope.getSolverScope());
        }
        workingRandom = null;
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingEnded(solverScope);
        if (cacheType == SelectionCacheType.SOLVER) {
            disposeCache(solverScope);
        }
    }

    protected void constructCache(DefaultSolverScope solverScope) {
        cachedValueList = new ArrayList<Object>(
                variableDescriptor.extractAllPlanningValues(solverScope.getWorkingSolution()));
    }

    protected void disposeCache(DefaultSolverScope solverScope) {
        cachedValueList = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return variableDescriptor.isContinuous();
    }

    public boolean isNeverEnding() {
        return randomSelection || isContinuous();
    }

    public long getSize() {
        return (long) cachedValueList.size();
    }

    public ValueIterator iterator() {
        if (!randomSelection) {
            return new IteratorToValueIteratorBridge(cachedValueList.iterator());
        } else {
            return new EntityIgnoringValueIterator() {
                public boolean hasNext() {
                    return true;
                }

                public Object next() {
                    int index = workingRandom.nextInt(cachedValueList.size());
                    return cachedValueList.get(index);
                }
            };
        }
    }

}
