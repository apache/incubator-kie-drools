/**
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

/**
 * @author Geoffrey De Smet
 */
public class StepCountTermination extends AbstractTermination {

    private int maximumStepCount = 100;

    public void setMaximumStepCount(int maximumStepCount) {
        this.maximumStepCount = maximumStepCount;
        if (maximumStepCount < 0) {
            throw new IllegalArgumentException("Property maximumStepCount (" + maximumStepCount
                    + ") must be greater or equal to 0.");
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isTerminated(LocalSearchStepScope localSearchStepScope) {
        int stepIndex = localSearchStepScope.getStepIndex();
        return stepIndex >= maximumStepCount;
    }

    public double calculateTimeGradient(LocalSearchStepScope localSearchStepScope) {
        int stepIndex = localSearchStepScope.getStepIndex();
        double timeGradient = ((double) stepIndex) / ((double) maximumStepCount);
        return Math.min(timeGradient, 1.0);
    }

}
