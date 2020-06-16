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

package org.optaplanner.core.impl.solver;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverStatus;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <ProblemId_> the ID type of a submitted problem, such as {@link Long} or {@link UUID}.
 */
public final class DefaultSolverJob<Solution_, ProblemId_> implements SolverJob<Solution_, ProblemId_>, Callable<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final DefaultSolverManager<Solution_, ProblemId_> solverManager;
    private final DefaultSolver<Solution_> solver;
    private final ProblemId_ problemId;
    private final Function<? super ProblemId_, ? extends Solution_> problemFinder;
    private final Consumer<? super Solution_> finalBestSolutionConsumer;
    private final BiConsumer<? super ProblemId_, ? super Throwable> exceptionHandler;

    private final AtomicReference<SolverStatus> solverStatusReference;
    private CountDownLatch terminatedLatch;

    private Future<Solution_> future;

    public DefaultSolverJob(
            DefaultSolverManager<Solution_, ProblemId_> solverManager,
            Solver<Solution_> solver, ProblemId_ problemId,
            Function<? super ProblemId_, ? extends Solution_> problemFinder,
            Consumer<? super Solution_> finalBestSolutionConsumer,
            BiConsumer<? super ProblemId_, ? super Throwable> exceptionHandler) {
        this.solverManager = solverManager;
        this.problemId = problemId;
        if (!(solver instanceof DefaultSolver)) {
            throw new IllegalStateException("Impossible state: solver is not instance of " +
                    DefaultSolver.class.getSimpleName() + ".");
        }
        this.solver = (DefaultSolver<Solution_>) solver;
        this.problemFinder = problemFinder;
        this.finalBestSolutionConsumer = finalBestSolutionConsumer;
        this.exceptionHandler = exceptionHandler;
        solverStatusReference = new AtomicReference<>(SolverStatus.SOLVING_SCHEDULED);
        terminatedLatch = new CountDownLatch(1);
    }

    public void setFuture(Future<Solution_> future) {
        this.future = future;
    }

    @Override
    public ProblemId_ getProblemId() {
        return problemId;
    }

    @Override
    public SolverStatus getSolverStatus() {
        return solverStatusReference.get();
    }

    @Override
    public Solution_ call() {
        SolverStatus solverStatus = solverStatusReference.getAndSet(SolverStatus.SOLVING_ACTIVE);
        if (solverStatus != SolverStatus.SOLVING_SCHEDULED) {
            // This job has been canceled before it started
            return problemFinder.apply(problemId);
        }
        try {
            Solution_ problem = problemFinder.apply(problemId);
            final Solution_ finalBestSolution = solver.solve(problem);
            if (finalBestSolutionConsumer != null) {
                // TODO consumption should happen on different thread than solver thread
                finalBestSolutionConsumer.accept(finalBestSolution);
            }
            return finalBestSolution;
        } catch (Exception e) {
            exceptionHandler.accept(problemId, e);
            throw new IllegalStateException("Solving failed for problemId (" + problemId + ").", e);
        } finally {
            solvingTerminated();
        }
    }

    private void solvingTerminated() {
        solverStatusReference.set(SolverStatus.NOT_SOLVING);
        solverManager.getProblemIdToSolverJobMap().remove(problemId);
        terminatedLatch.countDown();
    }

    // TODO Future features
    //    @Override
    //    public void reloadProblem(Function<? super ProblemId_, Solution_> problemFinder) {
    //        throw new UnsupportedOperationException("The solver is still solving and reloadProblem() is not yet supported.");
    //    }

    // TODO Future features
    //    @Override
    //    public void addProblemFactChange(ProblemFactChange<Solution_> problemFactChange) {
    //        solver.addProblemFactChange(problemFactChange);
    //    }

    @Override
    public void terminateEarly() {
        future.cancel(false);
        SolverStatus solverStatus = solverStatusReference.get();
        switch (solverStatus) {
            case SOLVING_SCHEDULED:
                solvingTerminated();
                break;
            case SOLVING_ACTIVE:
                // Indirectly triggers solvingTerminated()
                solver.terminateEarly();
                break;
            case NOT_SOLVING:
                // Do nothing, solvingTerminated() already called
                break;
            default:
                throw new IllegalStateException("Unsupported solverStatus (" + solverStatus + ").");
        }
        try {
            // Don't return until bestSolutionConsumer won't be called any more
            terminatedLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("The terminateEarly() call is interrupted.", e);
        }
    }

    @Override
    public Solution_ getFinalBestSolution() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public Duration getSolvingDuration() {
        SolverScope<Solution_> solverScope = solver.getSolverScope();
        Long startingSystemTimeMillis = solverScope.getStartingSystemTimeMillis();
        if (startingSystemTimeMillis == null) {
            // The solver hasn't started yet
            return Duration.ZERO;
        }
        Long endingSystemTimeMillis = solverScope.getEndingSystemTimeMillis();
        if (endingSystemTimeMillis == null) {
            // The solver hasn't ended yet
            endingSystemTimeMillis = System.currentTimeMillis();
        }
        return Duration.ofMillis(endingSystemTimeMillis - startingSystemTimeMillis);
    }
}
