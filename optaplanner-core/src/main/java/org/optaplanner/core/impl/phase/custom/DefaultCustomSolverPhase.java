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

package org.optaplanner.core.impl.phase.custom;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.phase.AbstractSolverPhase;
import org.optaplanner.core.impl.phase.custom.scope.CustomSolverPhaseScope;
import org.optaplanner.core.impl.phase.custom.scope.CustomStepScope;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Default implementation of {@link CustomSolverPhase}.
 */
public class DefaultCustomSolverPhase extends AbstractSolverPhase
        implements CustomSolverPhase {

    protected List<CustomSolverPhaseCommand> customSolverPhaseCommandList;
    protected boolean forceUpdateBestSolution;

    public void setCustomSolverPhaseCommandList(List<CustomSolverPhaseCommand> customSolverPhaseCommandList) {
        this.customSolverPhaseCommandList = customSolverPhaseCommandList;
    }

    public void setForceUpdateBestSolution(boolean forceUpdateBestSolution) {
        this.forceUpdateBestSolution = forceUpdateBestSolution;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solve(DefaultSolverScope solverScope) {
        CustomSolverPhaseScope customSolverPhaseScope = new CustomSolverPhaseScope(solverScope);
        phaseStarted(customSolverPhaseScope);

        CustomStepScope stepScope = createNextStepScope(customSolverPhaseScope, null);
        Iterator<CustomSolverPhaseCommand> commandIterator = customSolverPhaseCommandList.iterator();
        while (!termination.isPhaseTerminated(customSolverPhaseScope) && commandIterator.hasNext()) {
            CustomSolverPhaseCommand customSolverPhaseCommand = commandIterator.next();
            stepStarted(stepScope);
            customSolverPhaseCommand.changeWorkingSolution(solverScope.getScoreDirector());
            int uninitializedVariableCount = solverScope.getSolutionDescriptor()
                    .countUninitializedVariables(stepScope.getWorkingSolution());
            stepScope.setUninitializedVariableCount(uninitializedVariableCount);
            Score score = customSolverPhaseScope.calculateScore();
            stepScope.setScore(score);
            stepEnded(stepScope);
            stepScope = createNextStepScope(customSolverPhaseScope, stepScope);
        }
        phaseEnded(customSolverPhaseScope);
    }

    private CustomStepScope createNextStepScope(CustomSolverPhaseScope phaseScope, CustomStepScope completedStepScope) {
        if (completedStepScope == null) {
            completedStepScope = new CustomStepScope(phaseScope);
            completedStepScope.setScore(phaseScope.getStartingScore());
            completedStepScope.setStepIndex(-1);
        }
        phaseScope.setLastCompletedStepScope(completedStepScope);
        CustomStepScope stepScope = new CustomStepScope(phaseScope);
        stepScope.setStepIndex(completedStepScope.getStepIndex() + 1);
        return stepScope;
    }

    public void phaseStarted(CustomSolverPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
    }

    public void stepStarted(CustomStepScope stepScope) {
        super.stepStarted(stepScope);
    }

    public void stepEnded(CustomStepScope stepScope) {
        super.stepEnded(stepScope);
        boolean bestScoreImproved = stepScope.getBestScoreImproved();
        if (forceUpdateBestSolution && !bestScoreImproved) {
            DefaultSolverScope solverScope = stepScope.getPhaseScope().getSolverScope();
            Solution newBestSolution = solverScope.getScoreDirector().cloneWorkingSolution();
            bestSolutionRecaller.updateBestSolution(solverScope,
                    newBestSolution, stepScope.getUninitializedVariableCount());
        }
        CustomSolverPhaseScope customSolverPhaseScope = stepScope.getPhaseScope();
        if (logger.isDebugEnabled()) {
            long timeMillisSpend = customSolverPhaseScope.calculateSolverTimeMillisSpend();
            logger.debug("    Step index ({}), time spend ({}), score ({}), {} best score ({}).",
                    stepScope.getStepIndex(), timeMillisSpend,
                    stepScope.getScore(),
                    bestScoreImproved ? "new" : (forceUpdateBestSolution ? "forced" : "   "),
                    customSolverPhaseScope.getBestScore());
        }
    }

    public void phaseEnded(CustomSolverPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        logger.info("Phase ({}) custom ended: step total ({}), time spend ({}), best score ({}).",
                phaseIndex,
                phaseScope.getLastCompletedStepScope().getStepIndex() + 1,
                phaseScope.calculateSolverTimeMillisSpend(),
                phaseScope.getBestScore());
    }

}
