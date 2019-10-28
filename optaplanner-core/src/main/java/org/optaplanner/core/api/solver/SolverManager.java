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

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.DefaultSolverManager;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface SolverManager<Solution_> extends AutoCloseable {

    static <Solution_> SolverManager<Solution_> create(SolverConfig solverConfig) {
        SolverFactory<Solution_> solverFactory = SolverFactory.create(solverConfig);
        return new DefaultSolverManager<>(solverFactory);
    }

    default UUID solve(Solution_ planningProblem) {
        UUID problemId = UUID.randomUUID();
        solve(problemId, planningProblem);
        return problemId;
    }

    void solve(Object problemId, Solution_ planningProblem);

    // TODO onExceptionThrown default to logger.error
//    void solve(Object problemId,
//            Solution_ planningProblem,
//            Consumer<Solution_> onBestSolutionChanged,
//            Consumer<Solution_> onSolvingTerminated,
//            Consumer<Throwable> onExceptionThrown);

}
