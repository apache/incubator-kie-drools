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

package org.drools.planner.core.localsearch.decider.acceptor.greatdeluge;

import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.MoveScope;
import org.drools.planner.core.localsearch.decider.acceptor.AbstractAcceptor;
import org.drools.planner.core.score.Score;

/**
 * TODO Under construction. Feel free to create a patch to improve this acceptor!
 */
public class GreatDelugeAcceptor extends AbstractAcceptor {

    protected final double waterLevelUpperBoundRate;
    protected final double waterRisingRate;
    // TODO lowerboundRate when waterLevel rises on every MoveScope (not just every step) to reset waterlevel to upperbound
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
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        if (waterLevelUpperBoundRate < 1.0) {
            throw new IllegalArgumentException("The greatDelugeWaterLevelUpperBoundRate (" + waterLevelUpperBoundRate
                    + ") should be 1.0 or higher.");
        }
        if (waterRisingRate <= 0.0 || waterRisingRate >= 1.0) {
            throw new IllegalArgumentException("The greatDelugeWaterRisingRate (" + waterRisingRate
                    + ") should be between 0.0 and 1.0 (preferably very close to 0.0).");
        }
        waterLevelScore = localSearchSolverPhaseScope.getBestScore().multiply(waterLevelUpperBoundRate);
        Score perfectMaximumScore = localSearchSolverPhaseScope.getScoreDefinition().getPerfectMaximumScore();
        if (waterLevelScore.compareTo(perfectMaximumScore) > 0) {
            throw new IllegalArgumentException("The waterLevelScore (" + waterLevelScore
                    + ") should not be higher than the perfectMaximumScore(" + perfectMaximumScore + ").");
        }
    }

    public boolean isAccepted(MoveScope moveScope) {
        if (moveScope.getScore().compareTo(waterLevelScore) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void stepEnded(LocalSearchStepScope localSearchStepScope) {
        if (localSearchStepScope.getStepIndex() == localSearchStepScope.getPhaseScope().getBestSolutionStepIndex()) {
            // New best score
            waterLevelScore = localSearchStepScope.getPhaseScope().getBestScore().multiply(waterLevelUpperBoundRate);
        } else {
            Score perfectMaximumScore = localSearchStepScope.getPhaseScope().getScoreDefinition()
                    .getPerfectMaximumScore();
            Score waterLevelAugend = perfectMaximumScore.subtract(waterLevelScore).multiply(waterRisingRate);
            waterLevelScore = waterLevelScore.add(waterLevelAugend);
            // TODO maybe if waterlevel is higher than bestScore, than ...
        }
    }

}
