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

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * Represents a {@link PlanningSolution problem} that has been submitted to solve on the {@link SolverManager}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <ProblemId_> the ID type of a submitted problem, such as {@link Long} or {@link UUID}.
 */
public interface SolverJob<Solution_, ProblemId_> {

    /**
     * @return never null, a value given to {@link SolverManager#solve(Object, Function, Consumer)}
     *         or {@link SolverManager#solveAndListen(Object, Function, Consumer)}
     */
    ProblemId_ getProblemId();

    /**
     * Returns whether the {@link Solver} is scheduled to solve, actively solving or not.
     * <p>
     * Returns {@link SolverStatus#NOT_SOLVING} if the solver already terminated.
     *
     * @return never null
     */
    SolverStatus getSolverStatus();

    // TODO Future features
    //    void reloadProblem(Function<? super ProblemId_, Solution_> problemFinder);

    // TODO Future features
    //    void addProblemFactChange(ProblemFactChange<Solution_> problemFactChange);

    /**
     * Terminates the solver or cancels the solver job if it hasn't (re)started yet.
     * <p>
     * Does nothing if the solver already terminated.
     * <p>
     * Waits for the termination or cancellation to complete before returning.
     * During termination, a {@code bestSolutionConsumer} could still be called (on a consumer thread),
     * before this method returns.
     */
    void terminateEarly();

    /**
     * Waits if necessary for the solver to complete and then returns the final best {@link PlanningSolution}.
     *
     * @return never null, but it could be the original uninitialized problem
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException if the computation threw an exception
     */
    Solution_ getFinalBestSolution() throws InterruptedException, ExecutionException;

    /**
     * Returns the {@link Duration} spent solving since the last start.
     * If it hasn't started it yet, it returns {@link Duration#ZERO}.
     * If it hasn't ended yet, it returns the time between the last start and now.
     * If it has ended already, it returns the time between the last start and the ending.
     *
     * @return the {@link Duration} spent solving since the last (re)start, at least 0
     */
    Duration getSolvingDuration();

}
