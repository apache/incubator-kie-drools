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

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.AbstractPhase;
import org.optaplanner.core.impl.phase.custom.scope.CustomPhaseScope;
import org.optaplanner.core.impl.phase.custom.scope.CustomStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Default implementation of {@link CustomPhase}.
 */
public class DefaultCustomPhase extends AbstractPhase
        implements CustomPhase {

    protected List<CustomPhaseCommand> customPhaseCommandList;
    protected boolean forceUpdateBestSolution;

    public void setCustomPhaseCommandList(List<CustomPhaseCommand> customPhaseCommandList) {
        this.customPhaseCommandList = customPhaseCommandList;
    }

    public void setForceUpdateBestSolution(boolean forceUpdateBestSolution) {
        this.forceUpdateBestSolution = forceUpdateBestSolution;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solve(DefaultSolverScope solverScope) {
        CustomPhaseScope phaseScope = new CustomPhaseScope(solverScope);
        phaseStarted(phaseScope);

        CustomStepScope stepScope = new CustomStepScope(phaseScope);
        Iterator<CustomPhaseCommand> commandIterator = customPhaseCommandList.iterator();
        while (!termination.isPhaseTerminated(phaseScope) && commandIterator.hasNext()) {
            CustomPhaseCommand customPhaseCommand = commandIterator.next();
            stepStarted(stepScope);
            doStep(stepScope, customPhaseCommand);
            stepEnded(stepScope);
            phaseScope.setLastCompletedStepScope(stepScope);
            stepScope = new CustomStepScope(phaseScope);
        }
        phaseEnded(phaseScope);
    }

    public void phaseStarted(CustomPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
    }

    public void stepStarted(CustomStepScope stepScope) {
        super.stepStarted(stepScope);
    }

    private void doStep(CustomStepScope stepScope, CustomPhaseCommand customPhaseCommand) {
        InnerScoreDirector scoreDirector = stepScope.getScoreDirector();
        customPhaseCommand.changeWorkingSolution(scoreDirector);
        int uninitializedVariableCount = scoreDirector.getSolutionDescriptor()
                .countUninitializedVariables(stepScope.getWorkingSolution());
        stepScope.setUninitializedVariableCount(uninitializedVariableCount);
        Score score = scoreDirector.calculateScore();
        stepScope.setScore(score);
        bestSolutionRecaller.processWorkingSolutionDuringStep(stepScope);
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
        CustomPhaseScope phaseScope = stepScope.getPhaseScope();
        if (logger.isDebugEnabled()) {
            long timeMillisSpent = phaseScope.calculateSolverTimeMillisSpent();
            logger.debug("    Custom step ({}), time spent ({}), score ({}), {} best score ({}).",
                    stepScope.getStepIndex(), timeMillisSpent,
                    stepScope.getScore(),
                    bestScoreImproved ? "new" : (forceUpdateBestSolution ? "forced" : "   "),
                    phaseScope.getBestScoreWithUninitializedPrefix());
        }
    }

    public void phaseEnded(CustomPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        logger.info("Custom phase ({}) ended: step total ({}), time spent ({}), best score ({}).",
                phaseIndex,
                phaseScope.getNextStepIndex(),
                phaseScope.calculateSolverTimeMillisSpent(),
                phaseScope.getBestScore());
    }

}
