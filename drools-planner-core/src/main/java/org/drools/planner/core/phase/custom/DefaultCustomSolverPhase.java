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

package org.drools.planner.core.phase.custom;

import java.util.Iterator;
import java.util.List;

import org.drools.planner.core.phase.AbstractSolverPhase;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * Default implementation of {@link CustomSolverPhase}.
 */
public class DefaultCustomSolverPhase extends AbstractSolverPhase
        implements CustomSolverPhase {

    protected List<CustomSolverPhaseCommand> customSolverPhaseCommandList;

    public void setCustomSolverPhaseCommandList(List<CustomSolverPhaseCommand> customSolverPhaseCommandList) {
        this.customSolverPhaseCommandList = customSolverPhaseCommandList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solve(DefaultSolverScope solverScope) {
        CustomSolverPhaseScope customSolverPhaseScope = new CustomSolverPhaseScope(solverScope);
        phaseStarted(customSolverPhaseScope);

        CustomStepScope customStepScope = createNextStepScope(customSolverPhaseScope, null);
        Iterator<CustomSolverPhaseCommand> commandIterator = customSolverPhaseCommandList.iterator();
        while (!termination.isPhaseTerminated(customSolverPhaseScope) && commandIterator.hasNext()) {
            CustomSolverPhaseCommand customSolverPhaseCommand = commandIterator.next();
            customSolverPhaseCommand.changeWorkingSolution(solverScope.getScoreDirector());
            Score score = customSolverPhaseScope.calculateScore();
            customStepScope.setScore(score);
            stepTaken(customStepScope);
            customStepScope = createNextStepScope(customSolverPhaseScope, customStepScope);
        }
        phaseEnded(customSolverPhaseScope);
    }

    private CustomStepScope createNextStepScope(CustomSolverPhaseScope customSolverPhaseScope, CustomStepScope completedCustomStepScope) {
        if (completedCustomStepScope == null) {
            completedCustomStepScope = new CustomStepScope(customSolverPhaseScope);
            completedCustomStepScope.setScore(customSolverPhaseScope.getStartingScore());
            completedCustomStepScope.setStepIndex(-1);
        }
        customSolverPhaseScope.setLastCompletedCustomStepScope(completedCustomStepScope);
        CustomStepScope customStepScope = new CustomStepScope(customSolverPhaseScope);
        customStepScope.setStepIndex(completedCustomStepScope.getStepIndex() + 1);
        customStepScope.setSolutionInitialized(true);
        return customStepScope;
    }

    public void phaseStarted(CustomSolverPhaseScope customSolverPhaseScope) {
        super.phaseStarted(customSolverPhaseScope);
    }

    public void stepTaken(CustomStepScope customStepScope) {
        super.stepTaken(customStepScope);
        CustomSolverPhaseScope customSolverPhaseScope = customStepScope.getCustomSolverPhaseScope();
        logger.debug("    Step index ({}), time spend ({}), score ({}), {} best score ({}).",
                new Object[]{customStepScope.getStepIndex(),
                        customSolverPhaseScope.calculateSolverTimeMillisSpend(),
                        customStepScope.getScore(), (customStepScope.getBestScoreImproved() ? "new" : "   "),
                        customSolverPhaseScope.getBestScore()});
    }

    public void phaseEnded(CustomSolverPhaseScope customSolverPhaseScope) {
        super.phaseEnded(customSolverPhaseScope);
        logger.info("Phase custom ended: step total ({}), time spend ({}), best score ({}).",
                new Object[]{customSolverPhaseScope.getLastCompletedStepScope().getStepIndex() + 1,
                customSolverPhaseScope.calculateSolverTimeMillisSpend(),
                customSolverPhaseScope.getBestScore()});
    }

}
