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
 * The time gradient implementation of simulated annealing.
 */
public class SimulatedAnnealingAcceptor extends AbstractAcceptor {

    protected Score startingTemperature;

    protected int partsLength = -1;
    protected double[] startingTemperatureParts;
    // No protected Score temperature do avoid rounding errors when using Score.multiply(double)
    protected double[] temperatureParts;

    protected double temperatureMinimum = 1.0E-100; // Double.MIN_NORMAL is E-308

    public void setStartingTemperature(Score startingTemperature) {
        this.startingTemperature = startingTemperature;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        for (double startingTemperaturePart : startingTemperature.toDoubleArray()) {
            if (startingTemperaturePart < 0.0) {
                throw new IllegalArgumentException("The startingTemperature (" + startingTemperature
                        + ") cannot have negative part (" + startingTemperaturePart + ").");
            }
        }
        startingTemperatureParts = startingTemperature.toDoubleArray();
        temperatureParts = startingTemperatureParts;
        partsLength = startingTemperatureParts.length;
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        startingTemperatureParts = null;
        temperatureParts = null;
        partsLength = -1;
    }

    public boolean isAccepted(MoveScope moveScope) {
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = moveScope.getLocalSearchStepScope().getLocalSearchSolverPhaseScope();
        Score lastStepScore = localSearchSolverPhaseScope.getLastCompletedLocalSearchStepScope().getScore();
        Score moveScore = moveScope.getScore();
        if (moveScore.compareTo(lastStepScore) >= 0) {
            return true;
        }
        Score scoreDifference = lastStepScore.subtract(moveScore);
        double acceptChance = 1.0;
        double[] scoreDifferenceParts = scoreDifference.toDoubleArray();
        for (int i = 0; i < partsLength; i++) {
            double scoreDifferencePart = scoreDifferenceParts[i];
            double temperaturePart = temperatureParts[i];
            double acceptChancePart;
            if (scoreDifferencePart <= 0.0) {
                // In this part it is moveScore is better than the lastStepScore, so do not disrupt the acceptChance
                acceptChancePart = 1.0;
            } else {
                acceptChancePart = Math.exp(-scoreDifferencePart / temperaturePart);
            }
            acceptChance *= acceptChancePart;
        }
        if (moveScope.getWorkingRandom().nextDouble() < acceptChance) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void stepEnded(LocalSearchStepScope localSearchStepScope) {
        super.stepEnded(localSearchStepScope);
        double timeGradient = localSearchStepScope.getTimeGradient();
        double reverseTimeGradient = 1.0 - timeGradient;
        temperatureParts = new double[partsLength];
        for (int i = 0; i < partsLength; i++) {
            temperatureParts[i] = startingTemperatureParts[i] * reverseTimeGradient;
            if (temperatureParts[i] < temperatureMinimum) {
                temperatureParts[i] = temperatureMinimum;
            }
        }
        // TODO implement reheating
    }

}
