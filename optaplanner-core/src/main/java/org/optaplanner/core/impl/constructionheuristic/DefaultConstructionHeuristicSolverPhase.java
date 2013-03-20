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

package org.optaplanner.core.impl.constructionheuristic;

import java.util.List;

import org.optaplanner.core.impl.constructionheuristic.placer.entity.EntityPlacer;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicSolverPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.phase.AbstractSolverPhase;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Default implementation of {@link ConstructionHeuristicSolverPhase}.
 */
public class DefaultConstructionHeuristicSolverPhase extends AbstractSolverPhase implements ConstructionHeuristicSolverPhase {

    protected List<EntityPlacer> entityPlacerList;

    protected boolean assertStepScoreIsUncorrupted = false;

    public void setEntityPlacerList(List<EntityPlacer> entityPlacerList) {
        this.entityPlacerList = entityPlacerList;
    }

    public void setAssertStepScoreIsUncorrupted(boolean assertStepScoreIsUncorrupted) {
        this.assertStepScoreIsUncorrupted = assertStepScoreIsUncorrupted;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solve(DefaultSolverScope solverScope) {
        ConstructionHeuristicSolverPhaseScope phaseScope = new ConstructionHeuristicSolverPhaseScope(solverScope);
        phaseStarted(phaseScope);

        // TODO FIXME
        if (entityPlacerList.size() != 1) {
            throw new UnsupportedOperationException();
        }
        // TODO HACK
        EntityPlacer entityPlacer = entityPlacerList.get(0);

        ConstructionHeuristicStepScope stepScope = createNextStepScope(phaseScope, null);
        while (!termination.isPhaseTerminated(phaseScope)) {
            stepStarted(stepScope);
            ConstructionHeuristicMoveScope moveScope =  entityPlacer.nominateMove(stepScope);
            if (moveScope == null) {
                // All entities have been placed
                break;
            } else {
                Move step = moveScope.getMove();
                stepScope.setStep(step);
                if (logger.isDebugEnabled()) {
                    stepScope.setStepString(step.toString());
                }
                stepScope.setUndoStep(moveScope.getUndoMove());
                stepScope.setScore(moveScope.getScore());
            }
            doStep(stepScope);
            stepEnded(stepScope);
            stepScope = createNextStepScope(phaseScope, stepScope);
        }
        phaseEnded(phaseScope);
    }

    private ConstructionHeuristicStepScope createNextStepScope(ConstructionHeuristicSolverPhaseScope phaseScope,
            ConstructionHeuristicStepScope completedStepScope) {
        if (completedStepScope == null) {
            completedStepScope = new ConstructionHeuristicStepScope(phaseScope);
            completedStepScope.setScore(phaseScope.getStartingScore());
            completedStepScope.setStepIndex(-1);
        }
        phaseScope.setLastCompletedStepScope(completedStepScope);
        ConstructionHeuristicStepScope stepScope = new ConstructionHeuristicStepScope(phaseScope);
        stepScope.setStepIndex(completedStepScope.getStepIndex() + 1);
        return stepScope;
    }

    private void doStep(ConstructionHeuristicStepScope stepScope) {
        ConstructionHeuristicSolverPhaseScope phaseScope = stepScope.getPhaseScope();
        stepScope.getStep().doMove(stepScope.getScoreDirector());
        // there is no need to recalculate the score, but we still need to set it
        phaseScope.getWorkingSolution().setScore(stepScope.getScore());
        if (assertStepScoreIsUncorrupted) {
            phaseScope.assertWorkingScoreFromScratch(stepScope.getScore());
            phaseScope.assertExpectedWorkingScore(stepScope.getScore());
        }
    }

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        for (EntityPlacer entityPlacer : entityPlacerList) {
            entityPlacer.solvingStarted(solverScope);
        }
    }

    public void phaseStarted(ConstructionHeuristicSolverPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        for (EntityPlacer entityPlacer : entityPlacerList) {
            entityPlacer.phaseStarted(phaseScope);
        }
    }

    public void stepStarted(ConstructionHeuristicStepScope stepScope) {
        super.stepStarted(stepScope);
        for (EntityPlacer entityPlacer : entityPlacerList) {
            entityPlacer.stepStarted(stepScope);
        }
    }

    public void stepEnded(ConstructionHeuristicStepScope stepScope) {
        super.stepEnded(stepScope);
        for (EntityPlacer entityPlacer : entityPlacerList) {
            entityPlacer.stepEnded(stepScope);
        }
        if (logger.isDebugEnabled()) {
            long timeMillisSpend = stepScope.getPhaseScope().calculateSolverTimeMillisSpend();
            logger.debug("    Step index ({}), time spend ({}), score ({}), selected move count ({})"
                    + " for constructing step ({}).",
                    stepScope.getStepIndex(), timeMillisSpend,
                    stepScope.getScore(),
                    stepScope.getSelectedMoveCount(),
                    stepScope.getStepString());
        }
    }

    public void phaseEnded(ConstructionHeuristicSolverPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        Solution newBestSolution = phaseScope.getScoreDirector().cloneWorkingSolution();
        int newBestUninitializedVariableCount = phaseScope.getSolutionDescriptor()
                .countUninitializedVariables(newBestSolution);
        bestSolutionRecaller.updateBestSolution(phaseScope.getSolverScope(),
                newBestSolution, newBestUninitializedVariableCount);
        for (EntityPlacer entityPlacer : entityPlacerList) {
            entityPlacer.phaseEnded(phaseScope);
        }
        logger.info("Phase ({}) constructionHeuristic ended: step total ({}), time spend ({}), best score ({}).",
                phaseIndex,
                phaseScope.getLastCompletedStepScope().getStepIndex() + 1,
                phaseScope.calculateSolverTimeMillisSpend(),
                phaseScope.getBestScore());
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        for (EntityPlacer entityPlacer : entityPlacerList) {
            entityPlacer.solvingEnded(solverScope);
        }
    }

}
