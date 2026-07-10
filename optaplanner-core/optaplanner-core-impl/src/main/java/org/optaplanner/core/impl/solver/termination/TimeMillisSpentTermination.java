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

package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

public class TimeMillisSpentTermination<Solution_> extends AbstractTermination<Solution_> {

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

    @Override
    public boolean isSolverTerminated(SolverScope<Solution_> solverScope) {
        long solverTimeMillisSpent = solverScope.calculateTimeMillisSpentUpToNow();
        return isTerminated(solverTimeMillisSpent);
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope<Solution_> phaseScope) {
        long phaseTimeMillisSpent = phaseScope.calculatePhaseTimeMillisSpentUpToNow();
        return isTerminated(phaseTimeMillisSpent);
    }

    protected boolean isTerminated(long timeMillisSpent) {
        return timeMillisSpent >= timeMillisSpentLimit;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(SolverScope<Solution_> solverScope) {
        long solverTimeMillisSpent = solverScope.calculateTimeMillisSpentUpToNow();
        return calculateTimeGradient(solverTimeMillisSpent);
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope<Solution_> phaseScope) {
        long phaseTimeMillisSpent = phaseScope.calculatePhaseTimeMillisSpentUpToNow();
        return calculateTimeGradient(phaseTimeMillisSpent);
    }

    protected double calculateTimeGradient(long timeMillisSpent) {
        double timeGradient = timeMillisSpent / ((double) timeMillisSpentLimit);
        return Math.min(timeGradient, 1.0);
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public TimeMillisSpentTermination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
        return new TimeMillisSpentTermination<>(timeMillisSpentLimit);
    }

    @Override
    public String toString() {
        return "TimeMillisSpent(" + timeMillisSpentLimit + ")";
    }

}
