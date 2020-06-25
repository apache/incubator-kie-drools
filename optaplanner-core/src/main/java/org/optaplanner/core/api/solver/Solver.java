/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.impl.solver.termination.Termination;

/**
 * A Solver solves a planning problem and returns the best solution found.
 * It's recommended to create a new Solver instance for each dataset.
 * <p>
 * To create a Solver, use {@link SolverFactory#buildSolver()}.
 * To solve a planning problem, call {@link #solve(Object)}.
 * To solve a planning problem without blocking the current thread, use {@link SolverManager} instead.
 * <p>
 * These methods are not thread-safe and should be called from the same thread,
 * except for the methods that are explicitly marked as thread-safe.
 * Note that despite that {@link #solve} is not thread-safe for clients of this class,
 * that method is free to do multithreading inside itself.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface Solver<Solution_> {

    /**
     * Solves the planning problem and returns the best solution encountered
     * (which might or might not be optimal, feasible or even initialized).
     * <p>
     * It can take seconds, minutes, even hours or days before this method returns,
     * depending on the {@link Termination} configuration.
     * To terminate a {@link Solver} early, call {@link #terminateEarly()}.
     *
     * @param problem never null, a {@link PlanningSolution}, usually its planning variables are uninitialized
     * @return never null, but it can return the original, uninitialized {@link PlanningSolution} with a null {@link Score}.
     * @see #terminateEarly()
     */
    Solution_ solve(Solution_ problem);

    /**
     * Notifies the solver that it should stop at its earliest convenience.
     * This method returns immediately, but it takes an undetermined time
     * for the {@link #solve} to actually return.
     * <p>
     * If the solver is running in daemon mode, this is the only way to terminate it normally.
     * <p>
     * This method is thread-safe.
     * It can only be called from a different thread
     * because the original thread is still calling {@link #solve(Object)}.
     *
     * @return true if successful, false if was already terminating or terminated
     * @see #isTerminateEarly()
     * @see Future#cancel(boolean)
     */
    boolean terminateEarly();

    /**
     * This method is thread-safe.
     *
     * @return true if the {@link #solve} method is still running.
     */
    boolean isSolving();

    /**
     * This method is thread-safe.
     *
     * @return true if terminateEarly has been called since the {@link Solver} started.
     * @see Future#isCancelled()
     */
    boolean isTerminateEarly();

    /**
     * Schedules a {@link ProblemFactChange} to be processed.
     * <p>
     * As a side-effect, this restarts the {@link Solver}, effectively resetting all {@link Termination}s,
     * but not {@link #terminateEarly()}.
     * <p>
     * This method is thread-safe.
     * Follows specifications of {@link BlockingQueue#add(Object)} with by default
     * a capacity of {@link Integer#MAX_VALUE}.
     *
     * @param problemFactChange never null
     * @return true (as specified by {@link Collection#add})
     * @see #addProblemFactChanges(List)
     */
    boolean addProblemFactChange(ProblemFactChange<Solution_> problemFactChange);

    /**
     * Schedules multiple {@link ProblemFactChange}s to be processed.
     * <p>
     * As a side-effect, this restarts the {@link Solver}, effectively resetting all {@link Termination}s,
     * but not {@link #terminateEarly()}.
     * <p>
     * This method is thread-safe.
     * Follows specifications of {@link BlockingQueue#addAll(Collection)} with by default
     * a capacity of {@link Integer#MAX_VALUE}.
     *
     * @param problemFactChangeList never null
     * @return true (as specified by {@link Collection#add})
     * @see #addProblemFactChange(ProblemFactChange)
     */
    boolean addProblemFactChanges(List<ProblemFactChange<Solution_>> problemFactChangeList);

    /**
     * Checks if all scheduled {@link ProblemFactChange}s have been processed.
     * <p>
     * This method is thread-safe.
     *
     * @return true if there are no {@link ProblemFactChange}s left to do
     */
    boolean isEveryProblemFactChangeProcessed();

    /**
     * @param eventListener never null
     */
    void addEventListener(SolverEventListener<Solution_> eventListener);

    /**
     * @param eventListener never null
     */
    void removeEventListener(SolverEventListener<Solution_> eventListener);

}
