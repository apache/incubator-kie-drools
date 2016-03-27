/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.phase;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.localsearch.DefaultLocalSearchPhase;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleSupport;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see DefaultLocalSearchPhase
 */
public abstract class AbstractPhase<Solution_> implements Phase<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected int phaseIndex = -1;

    protected Termination termination;
    protected BestSolutionRecaller<Solution_> bestSolutionRecaller;

    protected PhaseLifecycleSupport<Solution_> phaseLifecycleSupport = new PhaseLifecycleSupport<>();

    public Termination getTermination() {
        return termination;
    }

    public int getPhaseIndex() {
        return phaseIndex;
    }

    public void setPhaseIndex(int phaseIndex) {
        this.phaseIndex = phaseIndex;
    }

    public void setTermination(Termination termination) {
        this.termination = termination;
    }

    public void setBestSolutionRecaller(BestSolutionRecaller<Solution_> bestSolutionRecaller) {
        this.bestSolutionRecaller = bestSolutionRecaller;
    }

    public abstract String getPhaseTypeString();

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope<Solution_> solverScope) {
        // bestSolutionRecaller.solvingStarted(...) is called by DefaultSolver
        termination.solvingStarted(solverScope);
        phaseLifecycleSupport.fireSolvingStarted(solverScope);
    }

    @Override
    public void solvingEnded(DefaultSolverScope<Solution_> solverScope) {
        // bestSolutionRecaller.solvingEnded(...) is called by DefaultSolver
        termination.solvingEnded(solverScope);
        phaseLifecycleSupport.fireSolvingEnded(solverScope);
    }

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        phaseScope.reset();
        bestSolutionRecaller.phaseStarted(phaseScope);
        termination.phaseStarted(phaseScope);
        phaseLifecycleSupport.firePhaseStarted(phaseScope);
    }

    @Override
    public void stepStarted(AbstractStepScope<Solution_> stepScope) {
        bestSolutionRecaller.stepStarted(stepScope);
        termination.stepStarted(stepScope);
        phaseLifecycleSupport.fireStepStarted(stepScope);
    }

    @Override
    public void stepEnded(AbstractStepScope<Solution_> stepScope) {
        bestSolutionRecaller.stepEnded(stepScope);
        termination.stepEnded(stepScope);
        phaseLifecycleSupport.fireStepEnded(stepScope);
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        bestSolutionRecaller.phaseEnded(phaseScope);
        termination.phaseEnded(phaseScope);
        phaseLifecycleSupport.firePhaseEnded(phaseScope);
    }

    @Override
    public void addPhaseLifecycleListener(PhaseLifecycleListener<Solution_> phaseLifecycleListener) {
        phaseLifecycleSupport.addEventListener(phaseLifecycleListener);
    }

    @Override
    public void removePhaseLifecycleListener(PhaseLifecycleListener<Solution_> phaseLifecycleListener) {
        phaseLifecycleSupport.removeEventListener(phaseLifecycleListener);
    }

    // ************************************************************************
    // Assert methods
    // ************************************************************************

    protected void assertWorkingSolutionInitialized(AbstractPhaseScope<Solution_> phaseScope) {
        InnerScoreDirector<Solution_> scoreDirector = phaseScope.getScoreDirector();
        SolutionDescriptor<Solution_> solutionDescriptor = scoreDirector.getSolutionDescriptor();
        Solution_ workingSolution = scoreDirector.getWorkingSolution();
        for (Iterator<Object> it = solutionDescriptor.extractAllEntitiesIterator(workingSolution); it.hasNext();) {
            Object entity = it.next();
            if (!solutionDescriptor.isEntityInitializedOrImmovable(scoreDirector, entity)) {
                EntityDescriptor<Solution_> entityDescriptor
                        = solutionDescriptor.findEntityDescriptorOrFail(entity.getClass());
                String variableRef = null;
                for (GenuineVariableDescriptor<Solution_> variableDescriptor : entityDescriptor.getGenuineVariableDescriptors()) {
                    if (!variableDescriptor.isInitialized(entity)) {
                        variableRef = variableDescriptor.getSimpleEntityAndVariableName();
                        break;
                    }
                }
                throw new IllegalStateException(getPhaseTypeString() + " phase (" + phaseIndex
                        + ") needs to start from an initialized solution, but the planning variable (" + variableRef
                        + ") is uninitialized for the entity (" +  entity + ").\n"
                        + "  Initialize the solution by configuring a Construction Heuristic phase before this phase.");
            }
        }
    }

}
