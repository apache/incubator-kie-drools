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

package org.drools.planner.core.heuristic.selector.variable;

import java.util.Iterator;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.event.SolverPhaseLifecycleListener;
import org.drools.planner.core.phase.step.AbstractStepScope;

public class PlanningValueWalker implements SolverPhaseLifecycleListener {

    private final PlanningVariableDescriptor planningVariableDescriptor;
    private final PlanningValueSelector planningValueSelector;

    private WorkingMemory workingMemory;

    private Object planningEntity;
    private FactHandle planningEntityFactHandle;
    private Iterator<?> planningValueIterator;

    private boolean isFirstValue; // TODO remove and require partially initialized entity's support in score rules
    private Object workingValue;

    public PlanningValueWalker(PlanningVariableDescriptor planningVariableDescriptor,
            PlanningValueSelector planningValueSelector) {
        this.planningVariableDescriptor = planningVariableDescriptor;
        this.planningValueSelector = planningValueSelector;
    }

    public PlanningVariableDescriptor getPlanningVariableDescriptor() {
        return planningVariableDescriptor;
    }

    public Object getWorkingValue() {
        return workingValue;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        planningValueSelector.phaseStarted(solverPhaseScope);
        workingMemory = solverPhaseScope.getWorkingMemory();
    }

    public void beforeDeciding(AbstractStepScope stepScope) {
        planningValueSelector.beforeDeciding(stepScope);
    }

    public void stepDecided(AbstractStepScope stepScope) {
        planningValueSelector.stepDecided(stepScope);
    }

    public void stepTaken(AbstractStepScope stepScope) {
        planningValueSelector.stepTaken(stepScope);
    }

    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        planningValueSelector.phaseEnded(solverPhaseScope);
        workingMemory = null;
        workingValue = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void initWalk(Object planningEntity) {
        this.planningEntity = planningEntity;
        planningValueIterator = planningValueSelector.iterator(planningEntity);
        Object value = planningValueIterator.next();
        planningVariableDescriptor.setValue(planningEntity, value);
        isFirstValue = true;
        workingValue = value;
    }

    public void initPlanningEntityFactHandle(FactHandle planningEntityFactHandle) {
        this.planningEntityFactHandle = planningEntityFactHandle;
    }

    public boolean hasWalk() {
        if (isFirstValue) {
            return true;
        }
        return planningValueIterator.hasNext();
    }

    public void walk() {
        if (isFirstValue) {
            isFirstValue = false;
        } else {
            Object value = planningValueIterator.next();
            changeWorkingValue(value);
        }
    }

    public void resetWalk() {
        planningValueIterator = planningValueSelector.iterator(planningEntity);
        Object value = planningValueIterator.next();
        changeWorkingValue(value);
        workingValue = value;
    }

    private void changeWorkingValue(Object value) {
        planningVariableDescriptor.setValue(planningEntity, value);
        workingMemory.update(planningEntityFactHandle, planningEntity);
        workingValue = value;
    }

}
