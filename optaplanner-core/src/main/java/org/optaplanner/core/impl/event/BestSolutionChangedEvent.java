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

    private final long timeMillisSpend;
    private final Solution newBestSolution;

    /**
     * Internal API.
     * @param source never null
     * @param timeMillisSpend >= 0L
     * @param newBestSolution never null
     */
    public BestSolutionChangedEvent(Solver source, long timeMillisSpend, Solution newBestSolution) {
        super(source);
        this.timeMillisSpend = timeMillisSpend;
        this.newBestSolution = newBestSolution;
    }

    /**
     * @return the amount of millis spend since the solver started until that best solution was found
     */
    public long getTimeMillisSpend() {
        return timeMillisSpend;
    }

    /**
     * @return never null
     */
    public Solution getNewBestSolution() {
        return newBestSolution;
    }

}
