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

package org.drools.planner.core.localsearch.termination;

import org.drools.planner.core.localsearch.LocalSearchStepScope;

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

    private int calculateUnimprovedStepCount(LocalSearchStepScope localSearchStepScope) {
        int bestStepIndex = localSearchStepScope.getLocalSearchSolverScope().getBestSolutionStepIndex();
        int stepIndex = localSearchStepScope.getStepIndex();
        return stepIndex - bestStepIndex;
    }

    public boolean isTerminated(LocalSearchStepScope localSearchStepScope) {
        int unimprovedStepCount = calculateUnimprovedStepCount(localSearchStepScope);
        return unimprovedStepCount >= maximumUnimprovedStepCount;
    }

    public double calculateTimeGradient(LocalSearchStepScope localSearchStepScope) {
        int unimprovedStepCount = calculateUnimprovedStepCount(localSearchStepScope);
        double timeGradient = ((double) unimprovedStepCount) / ((double) maximumUnimprovedStepCount);
        return Math.min(timeGradient, 1.0);
    }

}
