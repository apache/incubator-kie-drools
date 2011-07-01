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

import org.drools.planner.core.phase.step.AbstractStepScope;

public class UnimprovedStepCountTermination extends AbstractTermination {

    private int maximumUnimprovedStepCount = 100;

    public void setMaximumUnimprovedStepCount(int maximumUnimprovedStepCount) {
        this.maximumUnimprovedStepCount = maximumUnimprovedStepCount;
        if (maximumUnimprovedStepCount < 0) {
            throw new IllegalArgumentException("Property maximumUnimprovedStepCount (" + maximumUnimprovedStepCount
                    + ") must be greater or equal to 0.");
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isTerminated(AbstractStepScope stepScope) {
        int unimprovedStepCount = calculateUnimprovedStepCount(stepScope);
        return unimprovedStepCount >= maximumUnimprovedStepCount;
    }

    public double calculateTimeGradient(AbstractStepScope stepScope) {
        int unimprovedStepCount = calculateUnimprovedStepCount(stepScope);
        double timeGradient = ((double) unimprovedStepCount) / ((double) maximumUnimprovedStepCount);
        return Math.min(timeGradient, 1.0);
    }

    private int calculateUnimprovedStepCount(AbstractStepScope stepScope) {
        int bestStepIndex = stepScope.getSolverPhaseScope().getBestSolutionStepIndex();
        int stepIndex = stepScope.getStepIndex();
        return stepIndex - bestStepIndex;
    }

}
