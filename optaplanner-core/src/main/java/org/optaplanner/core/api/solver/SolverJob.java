/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <ProblemId_> the ID type of a submitted problem, such as {@link Long} or {@link UUID}.
 */
public interface SolverJob<Solution_, ProblemId_> {

    /**
     * @return never null, a value given to {@link SolverManager#solveBatch(Object, Function, Consumer)}
     *or {@link SolverManager#solveObserving(Object, Function, Consumer)}
     */
    ProblemId_ getProblemId();

    /**
     * Returns if the {@link Solver} is scheduled to solve, actively solving or not.
     * <p>
     * Returns {@link SolverStatus#NOT_SOLVING} if the solver already terminated.
     * @return never null
     */
    SolverStatus getSolverStatus();

    // TODO Future features
//    void reloadProblem(Function<ProblemId_, Solution_> problemFinder);

    // TODO Future features
//    void addProblemFactChange(ProblemFactChange<Solution_> problemFactChange);

    /**
     * Terminates the solver or cancels the solver job if it hasn't (re)started yet.
     * <p>
     * Does nothing if the solver already terminated.
     */
    void terminateEarly();

}
