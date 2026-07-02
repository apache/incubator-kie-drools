/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class GreatDelugeAcceptor<Solution_> extends AbstractAcceptor<Solution_> {

    private Score initialWaterLevel;
    private Score waterLevelIncrementScore;
    private Double waterLevelIncrementRatio;

    private Score startingWaterLevel = null;

    private Score currentWaterLevel = null;
    private Double currentWaterLevelRatio = null;

    public Score getWaterLevelIncrementScore() {
        return this.waterLevelIncrementScore;
    }

    public void setWaterLevelIncrementScore(Score waterLevelIncrementScore) {
        this.waterLevelIncrementScore = waterLevelIncrementScore;
    }

    public Score getInitialWaterLevel() {
        return this.initialWaterLevel;
    }

    public void setInitialWaterLevel(Score initialLevel) {
        this.initialWaterLevel = initialLevel;
    }

    public Double getWaterLevelIncrementRatio() {
        return this.waterLevelIncrementRatio;
    }

    public void setWaterLevelIncrementRatio(Double waterLevelIncrementRatio) {
        this.waterLevelIncrementRatio = waterLevelIncrementRatio;
    }

    @Override
    public void phaseStarted(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        startingWaterLevel = initialWaterLevel != null ? initialWaterLevel : phaseScope.getBestScore();
        if (waterLevelIncrementRatio != null) {
            currentWaterLevelRatio = 0.0;
        }
        currentWaterLevel = startingWaterLevel;
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        startingWaterLevel = null;
        if (waterLevelIncrementRatio != null) {
            currentWaterLevelRatio = null;
        }
        currentWaterLevel = null;
    }

    @Override
    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        Score moveScore = moveScope.getScore();
        if (moveScore.compareTo(currentWaterLevel) >= 0) {
            return true;
        }
        Score lastStepScore = moveScope.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore();
        if (moveScore.compareTo(lastStepScore) > 0) {
            // Aspiration
            return true;
        }
        return false;
    }

    @Override
    public void stepEnded(LocalSearchStepScope<Solution_> stepScope) {
        super.stepEnded(stepScope);
        if (waterLevelIncrementScore != null) {
            currentWaterLevel = currentWaterLevel.add(waterLevelIncrementScore);
        } else {
            // Avoid numerical instability: SimpleScore.of(500).multiply(0.000_001) underflows to zero
            currentWaterLevelRatio += waterLevelIncrementRatio;
            currentWaterLevel = startingWaterLevel.add(
                    // TODO targetWaterLevel.subtract(startingWaterLevel).multiply(waterLevelIncrementRatio);
                    // Use startingWaterLevel.abs() to keep the number being positive.
                    startingWaterLevel.abs().multiply(currentWaterLevelRatio));
        }
    }

}
