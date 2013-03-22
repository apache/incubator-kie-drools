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

package org.optaplanner.core.impl.localsearch.decider.acceptor.simulatedannealing;

import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.api.score.Score;

/**
 * TODO this will be removed once the time gradient based {@link SimulatedAnnealingAcceptor} is always better.
 */
public class LegacySimulatedAnnealingAcceptor extends AbstractAcceptor {

    protected double startingTemperature = -1.0;
    protected double temperatureSurvival = 0.997;

    protected double temperature;

    public void setStartingTemperature(double startingTemperature) {
        this.startingTemperature = startingTemperature;
    }

    public void setTemperatureSurvival(double temperatureSurvival) {
        this.temperatureSurvival = temperatureSurvival;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope phaseScope) {
        if (startingTemperature <= 0.0) {
            throw new IllegalArgumentException("The startingTemperature (" + startingTemperature
                    + ") cannot be negative or zero.");
        }
        if (temperatureSurvival <= 0.0) {
            throw new IllegalArgumentException("The temperatureSurvival (" + temperatureSurvival
                    + ") cannot be negative or zero.");
        }
        temperature = startingTemperature;
    }

    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = moveScope.getStepScope().getPhaseScope();
        Score lastStepScore = localSearchSolverPhaseScope.getLastCompletedStepScope().getScore();
        Score moveScore = moveScope.getScore();
        if (moveScore.compareTo(lastStepScore) > 0) {
            return true;
        }
        Score scoreDifference = lastStepScore.subtract(moveScore);
        double[] scoreDifferenceLevels = scoreDifference.toDoubleLevels();
        for (int i = 0; i < scoreDifferenceLevels.length - 1; i++) {
            if (scoreDifferenceLevels[i] != 0) {
                // more hard constraints broken, ignore it for now
                return false;
            }
        }
        double diff = scoreDifferenceLevels[scoreDifferenceLevels.length - 1];
        double acceptChance = Math.exp(-diff / temperature);
        if (moveScope.getWorkingRandom().nextDouble() < acceptChance) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void stepEnded(LocalSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        temperature *= temperatureSurvival;
    }

}
