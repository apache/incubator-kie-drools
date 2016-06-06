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

import java.util.Iterator;

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
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see DefaultLocalSearchPhase
 */
public abstract class AbstractPhase<Solution_> implements Phase<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected int phaseIndex = -1;

    protected Termination termination;
    protected BestSolutionRecaller<Solution_> bestSolutionRecaller;

    /** Used for {@link DefaultSolver#addPhaseLifecycleListener(PhaseLifecycleListener)}. */
    protected PhaseLifecycleSupport<Solution_> solverPhaseLifecycleSupport;
    /** Used for {@link #addPhaseLifecycleListener(PhaseLifecycleListener)}. */
    protected PhaseLifecycleSupport<Solution_> phaseLifecycleSupport = new PhaseLifecycleSupport<>();

    protected boolean assertStepScoreFromScratch = false;
    protected boolean assertExpectedStepScore = false;
    protected boolean assertShadowVariablesAreNotStaleAfterStep = false;

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

    @Override
    public void setSolverPhaseLifecycleSupport(PhaseLifecycleSupport<Solution_> solverPhaseLifecycleSupport) {
        this.solverPhaseLifecycleSupport = solverPhaseLifecycleSupport;
    }

    public boolean isAssertStepScoreFromScratch() {
        return assertStepScoreFromScratch;
    }

    public void setAssertStepScoreFromScratch(boolean assertStepScoreFromScratch) {
        this.assertStepScoreFromScratch = assertStepScoreFromScratch;
    }

    public boolean isAssertExpectedStepScore() {
        return assertExpectedStepScore;
    }

    public void setAssertExpectedStepScore(boolean assertExpectedStepScore) {
        this.assertExpectedStepScore = assertExpectedStepScore;
    }

    public boolean isAssertShadowVariablesAreNotStaleAfterStep() {
        return assertShadowVariablesAreNotStaleAfterStep;
    }

    public void setAssertShadowVariablesAreNotStaleAfterStep(boolean assertShadowVariablesAreNotStaleAfterStep) {
        this.assertShadowVariablesAreNotStaleAfterStep = assertShadowVariablesAreNotStaleAfterStep;
    }

    public abstract String getPhaseTypeString();

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope<Solution_> solverScope) {
        // bestSolutionRecaller.solvingStarted(...) is called by DefaultSolver
        // solverPhaseLifecycleSupport.solvingStarted(...) is called by DefaultSolver
        termination.solvingStarted(solverScope);
        phaseLifecycleSupport.fireSolvingStarted(solverScope);
    }

    @Override
    public void solvingEnded(DefaultSolverScope<Solution_> solverScope) {
        // bestSolutionRecaller.solvingEnded(...) is called by DefaultSolver
        // solverPhaseLifecycleSupport.solvingEnded(...) is called by DefaultSolver
        termination.solvingEnded(solverScope);
        phaseLifecycleSupport.fireSolvingEnded(solverScope);
    }

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        phaseScope.startingNow();
        phaseScope.reset();
        bestSolutionRecaller.phaseStarted(phaseScope);
        solverPhaseLifecycleSupport.firePhaseStarted(phaseScope);
        termination.phaseStarted(phaseScope);
        phaseLifecycleSupport.firePhaseStarted(phaseScope);
    }

    @Override
    public void stepStarted(AbstractStepScope<Solution_> stepScope) {
        bestSolutionRecaller.stepStarted(stepScope);
        solverPhaseLifecycleSupport.fireStepStarted(stepScope);
        termination.stepStarted(stepScope);
        phaseLifecycleSupport.fireStepStarted(stepScope);
    }

    protected void predictWorkingStepScore(AbstractStepScope<Solution_> stepScope, Object completedAction) {
        AbstractPhaseScope<Solution_> phaseScope = stepScope.getPhaseScope();
        // There is no need to recalculate the score, but we still need to set it
        phaseScope.getSolutionDescriptor().setScore(phaseScope.getWorkingSolution(), stepScope.getScore());
        if (assertStepScoreFromScratch) {
            phaseScope.assertWorkingScoreFromScratch(stepScope.getScore(), completedAction);
        }
        if (assertExpectedStepScore) {
            phaseScope.assertExpectedWorkingScore(stepScope.getScore(), completedAction);
        }
        if (assertShadowVariablesAreNotStaleAfterStep) {
            phaseScope.assertShadowVariablesAreNotStale(stepScope.getScore(), completedAction);
        }
    }

    @Override
    public void stepEnded(AbstractStepScope<Solution_> stepScope) {
        bestSolutionRecaller.stepEnded(stepScope);
        solverPhaseLifecycleSupport.fireStepEnded(stepScope);
        termination.stepEnded(stepScope);
        phaseLifecycleSupport.fireStepEnded(stepScope);
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        bestSolutionRecaller.phaseEnded(phaseScope);
        solverPhaseLifecycleSupport.firePhaseEnded(phaseScope);
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
        if (!phaseScope.getStartingScore().isSolutionInitialized()) {
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

}
