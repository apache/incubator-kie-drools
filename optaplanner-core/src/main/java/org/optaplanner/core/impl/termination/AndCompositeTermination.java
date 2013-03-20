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

public class AndCompositeTermination extends AbstractCompositeTermination {

    public AndCompositeTermination() {
    }

    public AndCompositeTermination(Termination... terminations) {
        super(terminations);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @param solverScope never null
     * @return true if all the Terminations are terminated.
     */
    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        for (Termination termination : terminationList) {
            if (!termination.isSolverTerminated(solverScope)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param phaseScope never null
     * @return true if all the Terminations are terminated.
     */
    public boolean isPhaseTerminated(AbstractSolverPhaseScope phaseScope) {
        for (Termination termination : terminationList) {
            if (!termination.isPhaseTerminated(phaseScope)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the minimum timeGradient of all Terminations.
     * Not supported timeGradients (-1.0) are ignored.
     * @param solverScope never null
     * @return the minimum timeGradient of the Terminations.
     */
    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        double timeGradient = 1.0;
        for (Termination termination : terminationList) {
            double nextTimeGradient = termination.calculateSolverTimeGradient(solverScope);
            if (nextTimeGradient >= 0.0) {
                timeGradient = Math.min(timeGradient, nextTimeGradient);
            }
        }
        return timeGradient;
    }

    /**
     * Calculates the minimum timeGradient of all Terminations.
     * Not supported timeGradients (-1.0) are ignored.
     * @param phaseScope never null
     * @return the minimum timeGradient of the Terminations.
     */
    public double calculatePhaseTimeGradient(AbstractSolverPhaseScope phaseScope) {
        double timeGradient = 1.0;
        for (Termination termination : terminationList) {
            double nextTimeGradient = termination.calculatePhaseTimeGradient(phaseScope);
            if (nextTimeGradient >= 0.0) {
                timeGradient = Math.min(timeGradient, nextTimeGradient);
            }
        }
        return timeGradient;
    }

}
