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

package org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge;

import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.api.score.Score;

/**
 * TODO Under construction. Feel free to create a patch to improve this acceptor!
 */
public class GreatDelugeAcceptor extends AbstractAcceptor {

    protected final double waterLevelUpperBoundRate;
    protected final double waterRisingRate;
    // TODO lowerboundRate when waterLevel rises on every move (not just every step) to reset waterlevel to upperbound
//    protected final double waterLevelLowerBoundRate;

    protected Score waterLevelScore = null;

    public GreatDelugeAcceptor(double waterLevelUpperBoundRate, double waterRisingRate) {
        this.waterLevelUpperBoundRate = waterLevelUpperBoundRate;
        this.waterRisingRate = waterRisingRate;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope phaseScope) {
        if (waterLevelUpperBoundRate < 1.0) {
            throw new IllegalArgumentException("The greatDelugeWaterLevelUpperBoundRate (" + waterLevelUpperBoundRate
                    + ") should be 1.0 or higher.");
        }
        if (waterRisingRate <= 0.0 || waterRisingRate >= 1.0) {
            throw new IllegalArgumentException("The greatDelugeWaterRisingRate (" + waterRisingRate
                    + ") should be between 0.0 and 1.0 (preferably very close to 0.0).");
        }
        waterLevelScore = phaseScope.getBestScore().multiply(waterLevelUpperBoundRate);
        Score perfectMaximumScore = phaseScope.getScoreDefinition().getPerfectMaximumScore();
        if (waterLevelScore.compareTo(perfectMaximumScore) > 0) {
            throw new IllegalArgumentException("The waterLevelScore (" + waterLevelScore
                    + ") should not be higher than the perfectMaximumScore(" + perfectMaximumScore + ").");
        }
    }

    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        if (moveScope.getScore().compareTo(waterLevelScore) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void stepEnded(LocalSearchStepScope stepScope) {
        if (stepScope.getStepIndex() == stepScope.getPhaseScope().getBestSolutionStepIndex()) {
            // New best score
            waterLevelScore = stepScope.getPhaseScope().getBestScore().multiply(waterLevelUpperBoundRate);
        } else {
            Score perfectMaximumScore = stepScope.getPhaseScope().getScoreDefinition()
                    .getPerfectMaximumScore();
            Score waterLevelAugend = perfectMaximumScore.subtract(waterLevelScore).multiply(waterRisingRate);
            waterLevelScore = waterLevelScore.add(waterLevelAugend);
            // TODO maybe if waterlevel is higher than bestScore, than ...
        }
    }

}
