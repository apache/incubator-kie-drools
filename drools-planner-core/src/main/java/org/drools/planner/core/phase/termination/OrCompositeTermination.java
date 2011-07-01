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

package org.drools.planner.core.phase.termination;

import org.drools.planner.core.solver.AbstractStepScope;

public class OrCompositeTermination extends AbstractCompositeTermination {

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @param stepScope never null
     * @return true if any of the Termination is terminated.
     */
    public boolean isTerminated(AbstractStepScope stepScope) {
        for (Termination termination : terminationList) {
            if (termination.isTerminated(stepScope)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the minimum timeGradient of all Terminations.
     * Not supported timeGradients (-1.0) are ignored.
     * @param stepScope never null
     * @return the maximum timeGradient of the Terminations.
     */
    public double calculateTimeGradient(AbstractStepScope stepScope) {
        double timeGradient = 0.0;
        for (Termination termination : terminationList) {
            double nextTimeGradient = termination.calculateTimeGradient(stepScope);
            if (nextTimeGradient >= 0.0) {
                timeGradient = Math.max(timeGradient, nextTimeGradient);
            }
        }
        return timeGradient;
    }

}
