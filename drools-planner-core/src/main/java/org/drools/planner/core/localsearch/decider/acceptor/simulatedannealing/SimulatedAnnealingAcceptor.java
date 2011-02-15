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

import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.MoveScope;
import org.drools.planner.core.localsearch.decider.acceptor.AbstractAcceptor;
import org.drools.planner.core.score.Score;

/**
 * The time gradient implementation of simulated annealing.
 * @author Geoffrey De Smet
 */
public class SimulatedAnnealingAcceptor extends AbstractAcceptor {

    protected double startingTemperature = 1.0;

    protected double temperature;
    protected double temperatureMinimum = 1.0E-100; // Double.MIN_NORMAL is E-308 

    public void setStartingTemperature(double startingTemperature) {
        this.startingTemperature = startingTemperature;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(LocalSearchSolverScope localSearchSolverScope) {
        if (startingTemperature < 0.0) {
            throw new IllegalArgumentException("The startingTemperature (" + startingTemperature
                    + ") cannot be negative.");
        }
        if (startingTemperature < temperatureMinimum) {
            throw new IllegalArgumentException("The startingTemperature (" + startingTemperature
                    + ") cannot be less than the temperatureMinimum (" + temperatureMinimum + ").");
        }
        temperature = startingTemperature;
    }

    public double calculateAcceptChance(MoveScope moveScope) {
        LocalSearchSolverScope localSearchSolverScope = moveScope.getLocalSearchStepScope().getLocalSearchSolverScope();
        Score lastStepScore = localSearchSolverScope.getLastCompletedLocalSearchStepScope().getScore();
        Score moveScore = moveScope.getScore();
        if (moveScore.compareTo(lastStepScore) > 0) {
            return 1.0;
        }
        Score scoreDifference = lastStepScore.subtract(moveScore);
        // TODO don't abuse translateScoreToGraphValue
        // TODO do hard and soft separately and then average their acceptChance
        Double diff = localSearchSolverScope.getScoreDefinition().translateScoreToGraphValue(scoreDifference);
        if (diff == null) {
            // more hard constraints broken, ignore it for now
            return 0.0;
        }
        double acceptChance = Math.exp(-diff / temperature);
        if (moveScope.getWorkingRandom().nextDouble() < acceptChance) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    @Override
    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        super.stepTaken(localSearchStepScope);
        double timeGradient = localSearchStepScope.getTimeGradient();
        temperature = startingTemperature * (1.0 - timeGradient);
        if (temperature < temperatureMinimum) {
            temperature = temperatureMinimum;
        }
        // TODO implement reheating
    }

}
