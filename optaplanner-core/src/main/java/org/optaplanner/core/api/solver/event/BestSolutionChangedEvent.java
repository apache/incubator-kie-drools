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

package org.optaplanner.core.api.solver.event;

import java.util.EventObject;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.solver.ProblemFactChange;

/**
 * Delivered when the best {@link Solution} changes during solving.
 * Delivered in the solver thread (which is the thread that calls {@link Solver#solve(Solution)}.
 */
public class BestSolutionChangedEvent<SolutionG extends Solution> extends EventObject {

    private final Solver solver;
    private final long timeMillisSpent;
    private final SolutionG newBestSolution;
    private final int newUninitializedVariableCount;

    /**
     * @param solver never null
     * @param timeMillisSpent >= 0L
     * @param newBestSolution never null
     * @param newUninitializedVariableCount >= 0
     */
    public BestSolutionChangedEvent(Solver solver, long timeMillisSpent, SolutionG newBestSolution,
            int newUninitializedVariableCount) {
        super(solver);
        this.solver = solver;
        this.timeMillisSpent = timeMillisSpent;
        this.newBestSolution = newBestSolution;
        this.newUninitializedVariableCount = newUninitializedVariableCount;
    }

    /**
     * @return >= 0, the amount of millis spent since the {@link Solver} started
     * until {@link #getNewBestSolution()} was found
     */
    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    /**
     * Note that:
     * <ul>
     *     <li>In real-time planning, not all {@link ProblemFactChange}s might be processed:
     *     check {@link #isEveryProblemFactChangeProcessed()}</li>
     *     <li>this {@link Solution} might be uninitialized: check {@link #isNewBestSolutionInitialized()}</li>
     *     <li>this {@link Solution} might be infeasible: check {@link FeasibilityScore#isFeasible()}</li>
     * </ul>
     * @return never null
     */
    public SolutionG getNewBestSolution() {
        return newBestSolution;
    }

    /**
     * @see Solver#isEveryProblemFactChangeProcessed()
     */
    public boolean isEveryProblemFactChangeProcessed() {
        return solver.isEveryProblemFactChangeProcessed();
    }

    /**
     * @return true if all the planning entities have planning variables that are initialized.
     */
    public boolean isNewBestSolutionInitialized() {
        return newUninitializedVariableCount == 0;
    }

}
