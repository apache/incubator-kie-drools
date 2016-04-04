/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.localsearch.decider.forager.finalist;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

/**
 * Strategic oscillation, works well with Tabu search.
 * @see FinalistPodium
 */
public class StrategicOscillationByLevelFinalistPodium extends AbstractFinalistPodium {

    protected final boolean referenceBestScoreInsteadOfLastStepScore;

    protected Number[] referenceLevelNumbers;

    protected Score finalistScore;
    protected Number[] finalistLevelNumbers;

    public StrategicOscillationByLevelFinalistPodium(boolean referenceBestScoreInsteadOfLastStepScore) {
        this.referenceBestScoreInsteadOfLastStepScore = referenceBestScoreInsteadOfLastStepScore;
    }

    @Override
    public void stepStarted(LocalSearchStepScope stepScope) {
        super.stepStarted(stepScope);
        referenceLevelNumbers = referenceBestScoreInsteadOfLastStepScore
            ? stepScope.getPhaseScope().getBestScore().toLevelNumbers()
            : stepScope.getPhaseScope().getLastCompletedStepScope().getScore().toLevelNumbers();
        finalistScore = null;
        finalistLevelNumbers = null;
    }

    @Override
    public void addMove(LocalSearchMoveScope moveScope) {
        boolean accepted = moveScope.getAccepted();
        if (finalistIsAccepted && !accepted) {
            return;
        }
        if (accepted && !finalistIsAccepted) {
            finalistIsAccepted = true;
            finalistScore = null;
            finalistLevelNumbers = null;
        }
        Score moveScore = moveScope.getScore();
        Number[] moveLevelNumbers = moveScore.toLevelNumbers();
        int comparison = doComparison(moveScore, moveLevelNumbers);
        if (comparison > 0) {
            finalistScore = moveScore;
            finalistLevelNumbers = moveLevelNumbers;
            finalistList.clear();
            finalistList.add(moveScope);
        } else if (comparison == 0) {
            finalistList.add(moveScope);
        }
    }

    private int doComparison(Score moveScore, Number[] moveLevelNumbers) {
        if (finalistScore == null) {
            return 1;
        }
        for (int i = 0; i < referenceLevelNumbers.length; i++) {
            boolean moveIsHigher = ((Comparable) moveLevelNumbers[i]).compareTo(referenceLevelNumbers[i]) > 0;
            boolean finalistIsHigher = ((Comparable) finalistLevelNumbers[i]).compareTo(referenceLevelNumbers[i]) > 0;
            if (moveIsHigher) {
                if (finalistIsHigher) {
                    break;
                } else {
                    return 1;
                }
            } else if (finalistIsHigher) {
                return -1;
            }
        }
        return moveScore.compareTo(finalistScore);
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        referenceLevelNumbers = null;
        finalistScore = null;
        finalistLevelNumbers = null;
    }

}
