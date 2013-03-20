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

package org.optaplanner.core.impl.termination;

import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class TimeMillisSpendTermination extends AbstractTermination {

    private long maximumTimeMillisSpend;

    public void setMaximumTimeMillisSpend(long maximumTimeMillisSpend) {
        this.maximumTimeMillisSpend = maximumTimeMillisSpend;
        if (maximumTimeMillisSpend <= 0L) {
            throw new IllegalArgumentException("Property maximumTimeMillisSpend (" + maximumTimeMillisSpend
                    + ") must be greater than 0.");
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        long solverTimeMillisSpend = solverScope.calculateTimeMillisSpend();
        return isTerminated(solverTimeMillisSpend);
    }

    public boolean isPhaseTerminated(AbstractSolverPhaseScope phaseScope) {
        long phaseTimeMillisSpend = phaseScope.calculatePhaseTimeMillisSpend();
        return isTerminated(phaseTimeMillisSpend);
    }

    private boolean isTerminated(long timeMillisSpend) {
        return timeMillisSpend >= maximumTimeMillisSpend;
    }

    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        long solverTimeMillisSpend = solverScope.calculateTimeMillisSpend();
        return calculateTimeGradient(solverTimeMillisSpend);
    }

    public double calculatePhaseTimeGradient(AbstractSolverPhaseScope phaseScope) {
        long phaseTimeMillisSpend = phaseScope.calculatePhaseTimeMillisSpend();
        return calculateTimeGradient(phaseTimeMillisSpend);
    }

    private double calculateTimeGradient(double timeMillisSpend) {
        double timeGradient = ((double) timeMillisSpend) / ((double) maximumTimeMillisSpend);
        return Math.min(timeGradient, 1.0);
    }

}
