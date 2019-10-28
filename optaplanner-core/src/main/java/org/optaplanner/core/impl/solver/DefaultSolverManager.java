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

package org.optaplanner.core.impl.solver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;

public class DefaultSolverManager<Solution_> implements SolverManager<Solution_> {

    private final SolverFactory<Solution_> solverFactory;
    private ExecutorService executorService;

    public DefaultSolverManager(SolverFactory<Solution_> solverFactory) {
        this.solverFactory = solverFactory;
        validateSolverFactory();
        executorService = Executors.newSingleThreadExecutor();
    }

    private void validateSolverFactory() {
        solverFactory.buildSolver();
    }

    @Override
    public void solve(Object problemId, Solution_ planningProblem) {
        executorService.submit(() -> {
            try {
                Solver<Solution_> solver = solverFactory.buildSolver();
                solver.solve(planningProblem);
            } catch (Exception e) {
                e.printStackTrace(); // TODO generated
            }
        });
        // TODO generated
    }

    @Override
    public void close() throws Exception {
        executorService.shutdownNow();
    }
}
