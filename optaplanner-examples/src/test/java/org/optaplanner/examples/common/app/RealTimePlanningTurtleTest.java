/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.app;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.solver.ProblemFactChange;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class RealTimePlanningTurtleTest<Solution_> extends AbstractTurtleTest {

    public static final int FREQUENCY = 300;
    public static final long SPENT_LIMIT = 5000L;
    protected Solver<Solution_> solver;

    @Test
    public void realTimePlanning() throws InterruptedException, ExecutionException {
        checkRunTurtleTests();
        final SolverFactory<Solution_> solverFactory = buildSolverFactory();
        final Solution_ problem = readProblem();
        solver = solverFactory.buildSolver();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<?> solveFuture = executorService.submit(() -> runSolve(solver, problem));
        Future<?> changesFuture = executorService.submit(() -> runChanges());
        solveFuture.get();
        changesFuture.get();
    }

    protected SolverFactory<Solution_> buildSolverFactory() {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(createSolverConfigResource());
        solverConfig.setDaemon(true);
        solverConfig.setTerminationConfig(new TerminationConfig().withMillisecondsSpentLimit(SPENT_LIMIT));
        return SolverFactory.create(solverConfig);
    }

    protected abstract String createSolverConfigResource();

    protected abstract Solution_ readProblem();

    protected void runSolve(Solver<Solution_> solver, Solution_ problem) {
        solver.solve(problem);
    }

    protected void runChanges() {
        Random random = new Random(37);
        long startSystemTimeMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() - startSystemTimeMillis < 600_000L) {
            ProblemFactChange<Solution_> factChange = nextProblemFactChange(random);
            solver.addProblemFactChange(factChange);
            long sleepMillis = (long) random.nextInt(FREQUENCY);
            if (sleepMillis <= (FREQUENCY / 100)) {
                sleepMillis = SPENT_LIMIT + 500L;
            } else if (sleepMillis <= (FREQUENCY / 10)) {
                sleepMillis = 0;
            }
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                solver.terminateEarly();
                throw new IllegalStateException("runChanges() interrupted.", e);
            }
        }
        solver.terminateEarly();
    }

    protected abstract ProblemFactChange<Solution_> nextProblemFactChange(Random random);

}
