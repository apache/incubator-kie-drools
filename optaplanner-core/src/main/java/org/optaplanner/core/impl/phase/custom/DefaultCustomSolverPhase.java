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

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.AbstractSolverPhase;
import org.optaplanner.core.impl.phase.custom.scope.CustomSolverPhaseScope;
import org.optaplanner.core.impl.phase.custom.scope.CustomStepScope;
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
        CustomSolverPhaseScope phaseScope = new CustomSolverPhaseScope(solverScope);
        phaseStarted(phaseScope);

        CustomStepScope stepScope = new CustomStepScope(phaseScope);
        Iterator<CustomSolverPhaseCommand> commandIterator = customSolverPhaseCommandList.iterator();
        while (!termination.isPhaseTerminated(phaseScope) && commandIterator.hasNext()) {
            CustomSolverPhaseCommand customSolverPhaseCommand = commandIterator.next();
            stepStarted(stepScope);
            customSolverPhaseCommand.changeWorkingSolution(solverScope.getScoreDirector());
            int uninitializedVariableCount = solverScope.getSolutionDescriptor()
                    .countUninitializedVariables(stepScope.getWorkingSolution());
            stepScope.setUninitializedVariableCount(uninitializedVariableCount);
            Score score = phaseScope.calculateScore();
            stepScope.setScore(score);
            stepEnded(stepScope);
            phaseScope.setLastCompletedStepScope(stepScope);
            stepScope = new CustomStepScope(phaseScope);
        }
        phaseEnded(phaseScope);
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
        CustomSolverPhaseScope phaseScope = stepScope.getPhaseScope();
        if (logger.isDebugEnabled()) {
            long timeMillisSpent = phaseScope.calculateSolverTimeMillisSpent();
            logger.debug("    Custom step ({}), time spent ({}), score ({}), {} best score ({}).",
                    stepScope.getStepIndex(), timeMillisSpent,
                    stepScope.getScore(),
                    bestScoreImproved ? "new" : (forceUpdateBestSolution ? "forced" : "   "),
                    phaseScope.getBestScoreWithUninitializedPrefix());
        }
    }

    public void phaseEnded(CustomSolverPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        logger.info("Custom phase ({}) ended: step total ({}), time spent ({}), best score ({}).",
                phaseIndex,
                phaseScope.getNextStepIndex(),
                phaseScope.calculateSolverTimeMillisSpent(),
                phaseScope.getBestScore());
    }

}
