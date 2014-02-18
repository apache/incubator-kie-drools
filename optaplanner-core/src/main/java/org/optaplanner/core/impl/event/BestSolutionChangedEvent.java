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

package org.optaplanner.core.impl.event;

import java.util.EventObject;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.solution.Solution;

/**
 * Delivered when the best solution changes during solving.
 */
public class BestSolutionChangedEvent extends EventObject {

    private final long timeMillisSpent;
    private final Solution newBestSolution;

    /**
     * @param source never null
     * @param timeMillisSpent >= 0L
     * @param newBestSolution never null
     */
    protected BestSolutionChangedEvent(Solver source, long timeMillisSpent, Solution newBestSolution) {
        super(source);
        this.timeMillisSpent = timeMillisSpent;
        this.newBestSolution = newBestSolution;
    }

    /**
     * @return the amount of millis spent since the solver started until {@link #getNewBestSolution()} was found
     */
    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    /**
     * @return never null
     */
    public Solution getNewBestSolution() {
        return newBestSolution;
    }

}
