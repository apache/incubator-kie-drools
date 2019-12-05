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

package org.optaplanner.spring.boot.example.poc.api.solver;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.spring.boot.example.poc.impl.solver.DefaultSolverManager;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <ProblemId_> the ID type of a submitted problem, such as {@link Long} or {@link UUID}.
 */
public interface SolverManager<Solution_, ProblemId_> extends AutoCloseable {

    static <Solution_, ProblemId_> SolverManager<Solution_, ProblemId_> create(SolverFactory<Solution_> solverFactory) {
        return new DefaultSolverManager<>(solverFactory);
    }

    // Syntactic sugar. Really needed? When in doubt, leave it out
//    default SolverFuture solve(Solution_ problem,
//            Consumer<Solution_> bestSolutionConsumer) {
//        return solve(() -> problem, bestSolutionConsumer);
//    }

    // Syntactic sugar. Really needed? When in doubt, leave it out
//    SolverJob<Solution_, UUID> solveObserving(Supplier<Solution_> problemSupplier,
//            Consumer<Solution_> bestSolutionConsumer);

    SolverJob<Solution_, ProblemId_> solveObserving(ProblemId_ problemId, Supplier<Solution_> problemSupplier,
            Consumer<Solution_> bestSolutionConsumer);

    SolverStatus getSolverStatus(ProblemId_ problemId);

    void reloadProblem(ProblemId_ problemId, Supplier<Solution_> problemSupplier);

    void addProblemFactChange(ProblemId_ problemId, ProblemFactChange<Solution_> problemFactChange);

    void terminateEarly(ProblemId_ problemId);

    void updateScore(Solution_ solution);

}
