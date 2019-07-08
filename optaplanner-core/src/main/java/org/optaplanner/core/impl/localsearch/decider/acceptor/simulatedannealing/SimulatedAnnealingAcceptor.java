/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.localsearch.decider.acceptor.simulatedannealing;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.ScoreUtils;

/**
 * The time gradient implementation of simulated annealing.
 */
public class SimulatedAnnealingAcceptor extends AbstractAcceptor {

    protected Score startingTemperature;

    protected int levelsLength = -1;
    protected double[] startingTemperatureLevels;
    // No protected Score temperature do avoid rounding errors when using Score.multiply(double)
    protected double[] temperatureLevels;

    protected double temperatureMinimum = 1.0E-100; // Double.MIN_NORMAL is E-308

    public void setStartingTemperature(Score startingTemperature) {
        this.startingTemperature = startingTemperature;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        for (double startingTemperatureLevel : ScoreUtils.extractLevelDoubles(startingTemperature)) {
            if (startingTemperatureLevel < 0.0) {
                throw new IllegalArgumentException("The startingTemperature (" + startingTemperature
                        + ") cannot have negative level (" + startingTemperatureLevel + ").");
            }
        }
        startingTemperatureLevels = ScoreUtils.extractLevelDoubles(startingTemperature);
        temperatureLevels = startingTemperatureLevels;
        levelsLength = startingTemperatureLevels.length;
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        startingTemperatureLevels = null;
        temperatureLevels = null;
        levelsLength = -1;
    }

    @Override
    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        LocalSearchPhaseScope phaseScope = moveScope.getStepScope().getPhaseScope();
        Score lastStepScore = phaseScope.getLastCompletedStepScope().getScore();
        Score moveScore = moveScope.getScore();
        if (moveScore.compareTo(lastStepScore) >= 0) {
            return true;
        }
        Score moveScoreDifference = lastStepScore.subtract(moveScore);
        double[] moveScoreDifferenceLevels = ScoreUtils.extractLevelDoubles(moveScoreDifference);
        double acceptChance = 1.0;
        for (int i = 0; i < levelsLength; i++) {
            double moveScoreDifferenceLevel = moveScoreDifferenceLevels[i];
            double temperatureLevel = temperatureLevels[i];
            double acceptChanceLevel;
            if (moveScoreDifferenceLevel <= 0.0) {
                // In this level, moveScore is better than the lastStepScore, so do not disrupt the acceptChance
                acceptChanceLevel = 1.0;
            } else {
                acceptChanceLevel = Math.exp(-moveScoreDifferenceLevel / temperatureLevel);
            }
            acceptChance *= acceptChanceLevel;
        }
        if (moveScope.getWorkingRandom().nextDouble() < acceptChance) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void stepStarted(LocalSearchStepScope stepScope) {
        super.stepStarted(stepScope);
        // TimeGradient only refreshes at the beginning of a step, so this code is in stepStarted instead of stepEnded
        double timeGradient = stepScope.getTimeGradient();
        double reverseTimeGradient = 1.0 - timeGradient;
        temperatureLevels = new double[levelsLength];
        for (int i = 0; i < levelsLength; i++) {
            temperatureLevels[i] = startingTemperatureLevels[i] * reverseTimeGradient;
            if (temperatureLevels[i] < temperatureMinimum) {
                temperatureLevels[i] = temperatureMinimum;
            }
        }
        // TODO implement reheating
    }

}
