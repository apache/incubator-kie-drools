/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.localsearch.decider.deciderscorecomparator;

import java.util.Comparator;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.comparator.FlatteningHardSoftScoreComparator;
import org.optaplanner.core.api.score.comparator.NaturalScoreComparator;

/**
 * @see DeciderScoreComparatorFactory
 */
public class ShiftingHardPenaltyDeciderScoreComparatorFactory extends AbstractDeciderScoreComparatorFactory {

    private int hardScoreActivationThreshold = 0;
    private int successiveNoHardChangeMinimum = 2;
    private int successiveNoHardChangeMaximum = 20;
    private double successiveNoHardChangeRepetitionMultiplicand = 20.0;
    private double hardWeightSurvivalRatio = 0.8;

    private int startingHardWeight = 1000; // TODO determine dynamically

    private int successiveNoHardScoreChange;
    private boolean shiftingPenaltyActive;
    private int hardWeight;

    private Comparator<Score> naturalDeciderScoreComparator = new NaturalScoreComparator();

    public void setHardScoreActivationThreshold(int hardScoreActivationThreshold) {
        this.hardScoreActivationThreshold = hardScoreActivationThreshold;
    }

    public void setSuccessiveNoHardChangeMinimum(int successiveNoHardChangeMinimum) {
        this.successiveNoHardChangeMinimum = successiveNoHardChangeMinimum;
    }

    public void setSuccessiveNoHardChangeMaximum(int successiveNoHardChangeMaximum) {
        this.successiveNoHardChangeMaximum = successiveNoHardChangeMaximum;
    }

    public void setSuccessiveNoHardChangeRepetitionMultiplicand(double successiveNoHardChangeRepetitionMultiplicand) {
        this.successiveNoHardChangeRepetitionMultiplicand = successiveNoHardChangeRepetitionMultiplicand;
    }

    public void setHardWeightSurvivalRatio(double hardWeightSurvivalRatio) {
        this.hardWeightSurvivalRatio = hardWeightSurvivalRatio;
    }

    public void setStartingHardWeight(int startingHardWeight) {
        this.startingHardWeight = startingHardWeight;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        successiveNoHardScoreChange = 0;
        shiftingPenaltyActive = false;
    }

    @Override
    public void stepEnded(LocalSearchStepScope localSearchStepScope) {
        if (localSearchStepScope.getStepIndex() == localSearchStepScope.getPhaseScope().getBestSolutionStepIndex()) {
            successiveNoHardScoreChange = 0;
            shiftingPenaltyActive = false;
        } else {
            HardSoftScore lastStepScore = (HardSoftScore) localSearchStepScope.getPhaseScope()
                    .getLastCompletedStepScope().getScore();
            HardSoftScore stepScore = (HardSoftScore) localSearchStepScope.getScore();
            if (stepScore.getHardScore() >= hardScoreActivationThreshold
                    && lastStepScore.getHardScore() == stepScore.getHardScore()) {
                successiveNoHardScoreChange++;
            } else {
                successiveNoHardScoreChange--;
                if (successiveNoHardScoreChange < 0) {
                    successiveNoHardScoreChange = 0;
                }
            }
            int min = successiveNoHardChangeMinimum;
            int max = successiveNoHardChangeMaximum;
            while (true) {
                if (successiveNoHardScoreChange < min) {
                    shiftingPenaltyActive = false;
                    break;
                } else if (successiveNoHardScoreChange <= max) {
                    shiftingPenaltyActive = true;
                    if (successiveNoHardScoreChange == min) {
                        hardWeight = startingHardWeight;
                    } else {
                        hardWeight = (int) Math.round(((double) hardWeight) * hardWeightSurvivalRatio);
                    }
                    break;
                }
                min = (int) Math.round(((double) min) * successiveNoHardChangeRepetitionMultiplicand);
                max = (int) Math.round(((double) max) * successiveNoHardChangeRepetitionMultiplicand);
            }
        }
    }

    public Comparator<Score> createDeciderScoreComparator() {
        if (shiftingPenaltyActive) {
            return new FlatteningHardSoftScoreComparator(hardWeight);
        } else {
            return naturalDeciderScoreComparator;
        }
    }

}
