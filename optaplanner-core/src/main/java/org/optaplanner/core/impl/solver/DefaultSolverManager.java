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
import java.util.function.Consumer;

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
    public DefaultSolverFuture solve(Solution_ planningProblem,
            Consumer<Solution_> bestSolutionConsumer) {
        Solver<Solution_> solver = solverFactory.buildSolver();
        // TODO consumption should happen on different thread than solver thread, doing skipAhead and throttling
        solver.addEventListener(event -> bestSolutionConsumer.accept(event.getNewBestSolution()));
        DefaultSolverFuture solverFuture = new DefaultSolverFuture<>(solver);
        executorService.submit(() -> {
            try {
                solver.solve(planningProblem);
            } catch (Exception e) {
                e.printStackTrace(); // TODO generated
            }
        });
        return solverFuture;
    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }

}
