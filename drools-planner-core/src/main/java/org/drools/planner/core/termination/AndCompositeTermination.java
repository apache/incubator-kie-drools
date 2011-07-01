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

package org.drools.planner.core.termination;

import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;

public class AndCompositeTermination extends AbstractCompositeTermination {

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @param lastSolverPhaseScope never null
     * @return true if all the Terminations are terminated.
     */
    public boolean isSolverTerminated(AbstractSolverPhaseScope lastSolverPhaseScope) {
        for (Termination termination : terminationList) {
            if (!termination.isSolverTerminated(lastSolverPhaseScope)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param stepScope never null
     * @return true if all the Terminations are terminated.
     */
    public boolean isPhaseTerminated(AbstractStepScope stepScope) {
        for (Termination termination : terminationList) {
            if (!termination.isPhaseTerminated(stepScope)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the minimum timeGradient of all Terminations.
     * Not supported timeGradients (-1.0) are ignored.
     * @param lastSolverPhaseScope never null
     * @return the minimum timeGradient of the Terminations.
     */
    public double calculateSolverTimeGradient(AbstractSolverPhaseScope lastSolverPhaseScope) {
        double timeGradient = 1.0;
        for (Termination termination : terminationList) {
            double nextTimeGradient = termination.calculateSolverTimeGradient(lastSolverPhaseScope);
            if (nextTimeGradient >= 0.0) {
                timeGradient = Math.min(timeGradient, nextTimeGradient);
            }
        }
        return timeGradient;
    }

    /**
     * Calculates the minimum timeGradient of all Terminations.
     * Not supported timeGradients (-1.0) are ignored.
     * @param stepScope never null
     * @return the minimum timeGradient of the Terminations.
     */
    public double calculatePhaseTimeGradient(AbstractStepScope stepScope) {
        double timeGradient = 1.0;
        for (Termination termination : terminationList) {
            double nextTimeGradient = termination.calculatePhaseTimeGradient(stepScope);
            if (nextTimeGradient >= 0.0) {
                timeGradient = Math.min(timeGradient, nextTimeGradient);
            }
        }
        return timeGradient;
    }

}
