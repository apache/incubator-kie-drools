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

package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class TimeMillisSpentTermination extends AbstractTermination {

    private final long timeMillisSpentLimit;

    public TimeMillisSpentTermination(long timeMillisSpentLimit) {
        this.timeMillisSpentLimit = timeMillisSpentLimit;
        if (timeMillisSpentLimit < 0L) {
            throw new IllegalArgumentException("The timeMillisSpentLimit (" + timeMillisSpentLimit
                    + ") cannot be negative.");
        }
    }

    public long getTimeMillisSpentLimit() {
        return timeMillisSpentLimit;
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        long solverTimeMillisSpent = solverScope.calculateTimeMillisSpent();
        return isTerminated(solverTimeMillisSpent);
    }

    public boolean isPhaseTerminated(AbstractPhaseScope phaseScope) {
        long phaseTimeMillisSpent = phaseScope.calculatePhaseTimeMillisSpent();
        return isTerminated(phaseTimeMillisSpent);
    }

    protected boolean isTerminated(long timeMillisSpent) {
        return timeMillisSpent >= timeMillisSpentLimit;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        long solverTimeMillisSpent = solverScope.calculateTimeMillisSpent();
        return calculateTimeGradient(solverTimeMillisSpent);
    }

    public double calculatePhaseTimeGradient(AbstractPhaseScope phaseScope) {
        long phaseTimeMillisSpent = phaseScope.calculatePhaseTimeMillisSpent();
        return calculateTimeGradient(phaseTimeMillisSpent);
    }

    protected double calculateTimeGradient(long timeMillisSpent) {
        double timeGradient = ((double) timeMillisSpent) / ((double) timeMillisSpentLimit);
        return Math.min(timeGradient, 1.0);
    }

}
