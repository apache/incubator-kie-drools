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

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.generic.GenericChainedChangeMove;
import org.drools.planner.core.move.generic.GenericChangeMove;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.event.SolverPhaseLifecycleListener;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.score.director.ScoreDirector;

public class PlanningValueWalker implements SolverPhaseLifecycleListener {

    private final PlanningVariableDescriptor planningVariableDescriptor;
    private final PlanningValueSelector planningValueSelector;

    private ScoreDirector scoreDirector;

    private Object planningEntity;
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

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        planningValueSelector.phaseStarted(solverPhaseScope);
        scoreDirector = solverPhaseScope.getScoreDirector();
    }

    public void stepStarted(AbstractStepScope stepScope) {
        planningValueSelector.stepStarted(stepScope);
    }

    public void stepEnded(AbstractStepScope stepScope) {
        planningValueSelector.stepEnded(stepScope);
    }

    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        planningValueSelector.phaseEnded(solverPhaseScope);
        scoreDirector = null;
        planningEntity = null;
        isFirstValue = false;
        workingValue = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void initWalk(Object planningEntity) {
        this.planningEntity = planningEntity;
        planningValueIterator = planningValueSelector.iterator(planningEntity);
        if (!planningValueIterator.hasNext()) {
            throw new IllegalStateException("The planningEntity (" + planningEntity + ") has a planning variable ("
                    + planningVariableDescriptor.getVariableName() + ") which has no planning values.");
        }
        Object value = planningValueIterator.next();
        planningVariableDescriptor.setValue(planningEntity, value);
        isFirstValue = true;
        workingValue = value;
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
        scoreDirector.beforeVariableChanged(planningEntity, planningVariableDescriptor.getVariableName());
        planningVariableDescriptor.setValue(planningEntity, value);
        scoreDirector.afterVariableChanged(planningEntity, planningVariableDescriptor.getVariableName());
        workingValue = value;
    }

    // TODO refactor variableWalker to this
    public Iterator<Move> moveIterator(final Object planningEntity) {
        final Iterator<?> planningValueIterator = planningValueSelector.iterator(planningEntity);
        if (!planningVariableDescriptor.isChained()) {
            return new ChangeMoveIterator(planningValueIterator, planningEntity);
        } else {
            return new ChainedChangeMoveIterator(planningValueIterator, planningEntity);
        }
    }

    private class ChangeMoveIterator implements Iterator<Move> {

        protected final Iterator<?> planningValueIterator;
        protected final Object planningEntity;

        public ChangeMoveIterator(Iterator<?> planningValueIterator, Object planningEntity) {
            this.planningValueIterator = planningValueIterator;
            this.planningEntity = planningEntity;
        }

        public boolean hasNext() {
            return planningValueIterator.hasNext();
        }

        public Move next() {
            Object toPlanningValue = planningValueIterator.next();
            return new GenericChangeMove(planningEntity, planningVariableDescriptor, toPlanningValue);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private class ChainedChangeMoveIterator extends ChangeMoveIterator {

        public ChainedChangeMoveIterator(Iterator<?> planningValueIterator, Object planningEntity) {
            super(planningValueIterator, planningEntity);
        }

        @Override
        public Move next() {
            Object toPlanningValue = planningValueIterator.next();
            return new GenericChainedChangeMove(planningEntity, planningVariableDescriptor, toPlanningValue);
        }

    }

}
