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

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.ChainedChangeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.phase.scope.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListener;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

@Deprecated
public class PlanningValueWalker implements SolverPhaseLifecycleListener {

    private final GenuineVariableDescriptor variableDescriptor;
    private final PlanningValueSelector planningValueSelector;

    private ScoreDirector scoreDirector;

    private Object entity;
    private Iterator<?> planningValueIterator;

    private boolean isFirstValue; // TODO remove and require partially initialized entity's support in score rules
    private Object workingValue;

    public PlanningValueWalker(GenuineVariableDescriptor variableDescriptor,
            PlanningValueSelector planningValueSelector) {
        this.variableDescriptor = variableDescriptor;
        this.planningValueSelector = planningValueSelector;
    }

    public GenuineVariableDescriptor getVariableDescriptor() {
        return variableDescriptor;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void solvingStarted(DefaultSolverScope solverScope) {
        planningValueSelector.solvingStarted(solverScope);
    }

    public void phaseStarted(AbstractSolverPhaseScope phaseScope) {
        planningValueSelector.phaseStarted(phaseScope);
        scoreDirector = phaseScope.getScoreDirector();
    }

    public void stepStarted(AbstractStepScope stepScope) {
        planningValueSelector.stepStarted(stepScope);
    }

    public void stepEnded(AbstractStepScope stepScope) {
        planningValueSelector.stepEnded(stepScope);
    }

    public void phaseEnded(AbstractSolverPhaseScope phaseScope) {
        planningValueSelector.phaseEnded(phaseScope);
        scoreDirector = null;
        entity = null;
        isFirstValue = false;
        workingValue = null;
    }

    public void solvingEnded(DefaultSolverScope solverScope) {
        planningValueSelector.solvingEnded(solverScope);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void initWalk(Object entity) {
        this.entity = entity;
        planningValueIterator = planningValueSelector.iterator(entity);
        if (!planningValueIterator.hasNext()) {
            throw new IllegalStateException("The entity (" + entity + ") has a planning variable ("
                    + variableDescriptor.getVariableName() + ") which has no planning values.");
        }
        Object value = planningValueIterator.next();
        scoreDirector.beforeVariableChanged(entity, variableDescriptor.getVariableName());
        variableDescriptor.setValue(entity, value);
        scoreDirector.afterVariableChanged(entity, variableDescriptor.getVariableName());
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
        planningValueIterator = planningValueSelector.iterator(entity);
        Object value = planningValueIterator.next();
        changeWorkingValue(value);
        workingValue = value;
    }

    private void changeWorkingValue(Object value) {
        scoreDirector.beforeVariableChanged(entity, variableDescriptor.getVariableName());
        variableDescriptor.setValue(entity, value);
        scoreDirector.afterVariableChanged(entity, variableDescriptor.getVariableName());
        workingValue = value;
    }

    // TODO refactor variableWalker to this
    public Iterator<Move> moveIterator(final Object entity) {
        final Iterator<?> planningValueIterator = planningValueSelector.iterator(entity);
        if (!variableDescriptor.isChained()) {
            return new ChangeMoveIterator(planningValueIterator, entity);
        } else {
            return new ChainedChangeMoveIterator(planningValueIterator, entity);
        }
    }

    private class ChangeMoveIterator extends SelectionIterator<Move> {

        protected final Iterator<?> planningValueIterator;
        protected final Object entity;

        public ChangeMoveIterator(Iterator<?> planningValueIterator, Object entity) {
            this.planningValueIterator = planningValueIterator;
            this.entity = entity;
        }

        public boolean hasNext() {
            return planningValueIterator.hasNext();
        }

        public Move next() {
            Object toPlanningValue = planningValueIterator.next();
            return new ChangeMove(entity, variableDescriptor, toPlanningValue);
        }

    }

    private class ChainedChangeMoveIterator extends ChangeMoveIterator {

        public ChainedChangeMoveIterator(Iterator<?> planningValueIterator, Object entity) {
            super(planningValueIterator, entity);
        }

        @Override
        public Move next() {
            Object toPlanningValue = planningValueIterator.next();
            return new ChainedChangeMove(entity, variableDescriptor, toPlanningValue);
        }

    }

}
