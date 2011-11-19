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

package org.drools.planner.core.localsearch.decider.acceptor.simulatedannealing;

import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.MoveScope;
import org.drools.planner.core.localsearch.decider.acceptor.AbstractAcceptor;
import org.drools.planner.core.score.Score;

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
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
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

    public boolean isAccepted(MoveScope moveScope) {
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = moveScope.getLocalSearchStepScope().getLocalSearchSolverPhaseScope();
        Score lastStepScore = localSearchSolverPhaseScope.getLastCompletedLocalSearchStepScope().getScore();
        Score moveScore = moveScope.getScore();
        if (moveScore.compareTo(lastStepScore) > 0) {
            return true;
        }
        Score scoreDifference = lastStepScore.subtract(moveScore);
        // TODO don't abuse translateScoreToGraphValue
        Double diff = localSearchSolverPhaseScope.getScoreDefinition().translateScoreToGraphValue(scoreDifference);
        if (diff == null) {
            // more hard constraints broken, ignore it for now
            return false;
        }
        double acceptChance = Math.exp(-diff / temperature);
        if (moveScope.getWorkingRandom().nextDouble() < acceptChance) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        super.stepTaken(localSearchStepScope);
        temperature *= temperatureSurvival;
    }

}
