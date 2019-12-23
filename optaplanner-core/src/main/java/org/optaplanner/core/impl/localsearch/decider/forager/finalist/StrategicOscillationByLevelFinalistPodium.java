/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
    protected Score referenceScore;
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
        referenceScore = referenceBestScoreInsteadOfLastStepScore
                ? stepScope.getPhaseScope().getBestScore()
                : stepScope.getPhaseScope().getLastCompletedStepScope().getScore();
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
        int comparison = doComparison(moveScope);
        if (comparison > 0) {
            finalistScore = moveScope.getScore();
            finalistLevelNumbers = moveScope.getScore().toLevelNumbers();
            finalistList.clear();
            finalistList.add(moveScope);
        } else if (comparison == 0) {
            finalistList.add(moveScope);
        }
    }

    private int doComparison(LocalSearchMoveScope moveScope) {
        if (finalistScore == null) {
            return 1;
        }

        Score moveScore = moveScope.getScore();
        Score bestStepScore = moveScope.getStepScope().getScore();
        boolean hasImproving = bestStepScore != null && bestStepScore.compareTo(moveScore) > 0;
        if (!hasImproving && moveScore.compareTo(referenceScore) > 0) {
            /*
             * Found an improving move.
             * The '!hasImproving' condition is present so that it is only checking for an improving move in this step
             * if it is not already found.
             * The part after the && sign will not be executed if 'hasImproving' is true.
             */
            hasImproving = true;
        }
        if (!hasImproving) {
            // There are no improving moves (including this one) so far. Checking is this a strategic oscillation move.
            Number[] moveLevelNumbers = moveScore.toLevelNumbers();
            for (int i = 0; i < referenceLevelNumbers.length; i++) {
                // True if it has an improvement at the current level.
                boolean moveIsHigher = compareLevelNumbersAgainstReference(moveLevelNumbers, i) > 0;
                boolean finalistIsHigher = compareLevelNumbersAgainstReference(finalistLevelNumbers, i) > 0;
                if (moveIsHigher) {
                    // Current move has improvement.
                    if (finalistIsHigher) {
                        // There is also an improvement produced in the previous moves.
                        break;
                    } else {
                        // This move is the first improving move for the i-th level at the current step.
                        return 1;
                    }
                } else if (finalistIsHigher) {
                    /*
                     * The current move is not producing an improving score at this level but there is already a
                     * previous move that does that so we definitely know that the previous finalists have a better
                     * score than this move.
                     */
                    return -1;
                }
            }
        }
        /*
         * If it comes to this point it means that both the finalists and the current move are improving the score
         * either at just one level (as strategic oscillation moves) or one of them or both might be an overall
         * improvement compared to the reference score, so now we compare which one is better.
         */
        return moveScore.compareTo(finalistScore);
    }

    private int compareLevelNumbersAgainstReference(Number[] actualLevelNumbers, int level) {
        return ((Comparable) actualLevelNumbers[level]).compareTo(referenceLevelNumbers[level]);
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        referenceLevelNumbers = null;
        finalistScore = null;
        finalistLevelNumbers = null;
    }
}
