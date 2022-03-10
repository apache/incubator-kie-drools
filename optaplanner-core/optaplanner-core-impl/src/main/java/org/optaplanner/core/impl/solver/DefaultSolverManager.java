/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <ProblemId_> the ID type of submitted problem, such as {@link Long} or {@link UUID}.
 */
public final class DefaultSolverManager<Solution_, ProblemId_> implements SolverManager<Solution_, ProblemId_> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSolverManager.class);

    private final BiConsumer<ProblemId_, Throwable> defaultExceptionHandler;
    private final SolverFactory<Solution_> solverFactory;
    private final ExecutorService solverThreadPool;
    private final ConcurrentMap<Object, DefaultSolverJob<Solution_, ProblemId_>> problemIdToSolverJobMap;

    public DefaultSolverManager(SolverFactory<Solution_> solverFactory,
            SolverManagerConfig solverManagerConfig) {
        defaultExceptionHandler = (problemId, throwable) -> LOGGER.error(
                "Solving failed for problemId ({}).", problemId, throwable);
        this.solverFactory = solverFactory;
        validateSolverFactory();
        int parallelSolverCount = solverManagerConfig.resolveParallelSolverCount();
        solverThreadPool = Executors.newFixedThreadPool(parallelSolverCount);
        problemIdToSolverJobMap = new ConcurrentHashMap<>(parallelSolverCount * 10);
    }

    public SolverFactory<Solution_> getSolverFactory() {
        return solverFactory;
    }

    private void validateSolverFactory() {
        solverFactory.buildSolver();
    }

    private ProblemId_ getProblemIdOrThrow(ProblemId_ problemId) {
        if (problemId != null) {
            return problemId;
        }
        throw new NullPointerException("Invalid problemId (null) given to SolverManager.");
    }

    private DefaultSolverJob<Solution_, ProblemId_> getSolverJob(ProblemId_ problemId) {
        return problemIdToSolverJobMap.get(getProblemIdOrThrow(problemId));
    }

    @Override
    public SolverJob<Solution_, ProblemId_> solve(ProblemId_ problemId,
            Function<? super ProblemId_, ? extends Solution_> problemFinder,
            Consumer<? super Solution_> finalBestSolutionConsumer,
            BiConsumer<? super ProblemId_, ? super Throwable> exceptionHandler) {
        return solve(getProblemIdOrThrow(problemId), problemFinder, null, finalBestSolutionConsumer, exceptionHandler);
    }

    @Override
    public SolverJob<Solution_, ProblemId_> solveAndListen(ProblemId_ problemId,
            Function<? super ProblemId_, ? extends Solution_> problemFinder,
            Consumer<? super Solution_> bestSolutionConsumer,
            Consumer<? super Solution_> finalBestSolutionConsumer,
            BiConsumer<? super ProblemId_, ? super Throwable> exceptionHandler) {
        return solve(getProblemIdOrThrow(problemId), problemFinder, bestSolutionConsumer, finalBestSolutionConsumer,
                exceptionHandler);
    }

    protected SolverJob<Solution_, ProblemId_> solve(ProblemId_ problemId,
            Function<? super ProblemId_, ? extends Solution_> problemFinder,
            Consumer<? super Solution_> bestSolutionConsumer,
            Consumer<? super Solution_> finalBestSolutionConsumer,
            BiConsumer<? super ProblemId_, ? super Throwable> exceptionHandler) {
        Solver<Solution_> solver = solverFactory.buildSolver();
        ((DefaultSolver<Solution_>) solver).setMonitorTagMap(Map.of("problem.id", problemId.toString()));
        BiConsumer<? super ProblemId_, ? super Throwable> finalExceptionHandler = (exceptionHandler != null)
                ? exceptionHandler
                : defaultExceptionHandler;
        DefaultSolverJob<Solution_, ProblemId_> solverJob = problemIdToSolverJobMap
                .compute(problemId, (key, oldSolverJob) -> {
                    if (oldSolverJob != null) {
                        // TODO Future features: automatically restart solving by calling reloadProblem()
                        throw new IllegalStateException("The problemId (" + problemId + ") is already solving.");
                    } else {
                        return new DefaultSolverJob<>(this, solver, problemId, problemFinder,
                                bestSolutionConsumer, finalBestSolutionConsumer, finalExceptionHandler);
                    }
                });
        Future<Solution_> future = solverThreadPool.submit(solverJob);
        solverJob.setFinalBestSolutionFuture(future);
        return solverJob;
    }

    @Override
    public SolverStatus getSolverStatus(ProblemId_ problemId) {
        DefaultSolverJob<Solution_, ProblemId_> solverJob = getSolverJob(problemId);
        if (solverJob == null) {
            return SolverStatus.NOT_SOLVING;
        }
        return solverJob.getSolverStatus();
    }

    // TODO Future features
    //    @Override
    //    public void reloadProblem(ProblemId_ problemId, Function<? super ProblemId_, Solution_> problemFinder) {
    //        DefaultSolverJob<Solution_, ProblemId_> solverJob = problemIdToSolverJobMap.get(problemId);
    //        if (solverJob == null) {
    //            // We cannot distinguish between "already terminated" and "never solved" without causing a memory leak.
    //            logger.debug("Ignoring reloadProblem() call because problemId ({}) is not solving.", problemId);
    //            return;
    //        }
    //        solverJob.reloadProblem(problemFinder);
    //    }

    @Override
    public void addProblemChange(ProblemId_ problemId, ProblemChange<Solution_> problemChange) {
        DefaultSolverJob<Solution_, ProblemId_> solverJob = getSolverJob(problemId);
        if (solverJob == null) {
            // We cannot distinguish between "already terminated" and "never solved" without causing a memory leak.
            throw new IllegalStateException(
                    "Cannot add the problem change (" + problemChange + ") because there is no solver solving the problemId ("
                            + problemId + ").");
        }
        solverJob.addProblemChange(problemChange);
    }

    @Override
    public void terminateEarly(ProblemId_ problemId) {
        DefaultSolverJob<Solution_, ProblemId_> solverJob = getSolverJob(problemId);
        if (solverJob == null) {
            // We cannot distinguish between "already terminated" and "never solved" without causing a memory leak.
            LOGGER.debug("Ignoring terminateEarly() call because problemId ({}) is not solving.", problemId);
            return;
        }
        solverJob.terminateEarly();
    }

    @Override
    public void close() {
        solverThreadPool.shutdownNow();
        problemIdToSolverJobMap.values().forEach(DefaultSolverJob::close);
    }

    void unregisterSolverJob(ProblemId_ problemId) {
        problemIdToSolverJobMap.remove(getProblemIdOrThrow(problemId));
    }

}
