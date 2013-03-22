/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.api.solver;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import org.optaplanner.core.impl.event.SolverEventListener;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.core.impl.termination.Termination;

/**
 * A Solver solves planning problems.
 * <p/>
 * Most methods are not thread-safe and should be called from the same thread.
 */
public interface Solver {

    /**
     * @param planningProblem never null
     */
    void setPlanningProblem(Solution planningProblem);

    /**
     * @return never null, but it can return the original, uninitialized {@link Solution} with a {@link Score} null.
     */
    Solution getBestSolution();

    /**
     * TODO timeMillisSpend should not continue to increase after the solver has been terminated
     * @return the amount of millis spend since this solver started
     */
    long getTimeMillisSpend();

    /**
     * Solves the planning problem.
     * It can take minutes, even hours or days before this method returns,
     * depending on the termination configuration.
     * To terminate a {@link Solver} early, call {@link #terminateEarly()}.
     * @see #terminateEarly()
     */
    void solve();

    /**
     * This method is thread-safe.
     * @return true if the {@link #solve()} method is still running.
     */
    boolean isSolving();

    /**
     * Notifies the solver that it should stop at its earliest convenience.
     * This method returns immediately, but it takes an undetermined time
     * for the {@link #solve()} to actually return.
     * <p/>
     * This method is thread-safe.
     * @return true if successful
     * @see #isTerminateEarly()
     * @see Future#cancel(boolean)
     */
    boolean terminateEarly();

    /**
     * This method is thread-safe.
     * @return true if terminateEarly has been called since the {@Solver} started.
     * @see Future#isCancelled()
     */
    boolean isTerminateEarly();

    /**
     * Schedules a {@link ProblemFactChange} to be processed.
     * <p/>
     * As a side-effect, this restarts the {@link Solver}, effectively resetting all {@link Termination}s,
     * but not {@link #terminateEarly()}.
     * <p/>
     * This method is thread-safe.
     * Follows specifications of {@link BlockingQueue#add(Object)} with by default
     * a capacity of {@link Integer#MAX_VALUE}.
     * @param problemFactChange never null
     * @return true (as specified by {@link Collection#add})
     */
    boolean addProblemFactChange(ProblemFactChange problemFactChange);

    /**
     * Checks if all scheduled {@link ProblemFactChange}s have been processed.
     * <p/>
     * This method is thread-safe.
     * @return true if there are no {@link ProblemFactChange}s left to do
     */
    boolean isEveryProblemFactChangeProcessed();

    /**
     * @param eventListener never null
     */
    void addEventListener(SolverEventListener eventListener);

    /**
     * @param eventListener never null
     */
    void removeEventListener(SolverEventListener eventListener);

    /**
     * @return never null
     */
    ScoreDirectorFactory getScoreDirectorFactory();

}
