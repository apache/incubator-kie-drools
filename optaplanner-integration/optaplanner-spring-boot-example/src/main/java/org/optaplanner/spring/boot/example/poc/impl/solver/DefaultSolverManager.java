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

package org.optaplanner.spring.boot.example.poc.impl.solver;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.spring.boot.example.poc.api.solver.SolverJob;
import org.optaplanner.spring.boot.example.poc.api.solver.SolverManager;
import org.optaplanner.spring.boot.example.poc.api.solver.SolverStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <ProblemId_> the ID type of a submitted problem, such as {@link Long} or {@link UUID}.
 */
public class DefaultSolverManager<Solution_, ProblemId_> implements SolverManager<Solution_, ProblemId_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final SolverFactory<Solution_> solverFactory;
    private ExecutorService executorService;

    private ConcurrentMap<Object, DefaultSolverJob<Solution_, ProblemId_>> problemIdToSolverJobMap;

    public DefaultSolverManager(SolverFactory<Solution_> solverFactory) {
        this.solverFactory = solverFactory;
        validateSolverFactory();
        int solverThreadCount = 1;
        executorService = Executors.newFixedThreadPool(solverThreadCount);
        problemIdToSolverJobMap = new ConcurrentHashMap<>(solverThreadCount * 10);
    }

    private void validateSolverFactory() {
        solverFactory.buildSolver();
    }

    @Override
    public SolverJob<Solution_, ProblemId_> solveObserving(ProblemId_ problemId,
            Supplier<Solution_> problemSupplier, Consumer<Solution_> bestSolutionConsumer) {
        Solver<Solution_> solver = solverFactory.buildSolver();
        // TODO consumption should happen on different thread than solver thread, doing skipAhead and throttling
        solver.addEventListener(event -> bestSolutionConsumer.accept(event.getNewBestSolution()));
        DefaultSolverJob<Solution_, ProblemId_> solverJob = problemIdToSolverJobMap
                .compute(problemId, (key, oldSolverJob) -> {
           if (oldSolverJob != null) {
               // TODO Handle gracefully
               throw new IllegalStateException("Already solving!");
           } else {
               return new DefaultSolverJob<>(problemId, solver);
           }
        });
        executorService.submit(() -> {
            try {
                Solution_ problem = problemSupplier.get();
                solver.solve(problem);
            } catch (Exception e) {
                // TODO
                e.printStackTrace();
            } finally {
                problemIdToSolverJobMap.remove(problemId);
            }
        });
        return solverJob;
    }

    @Override
    public SolverStatus getSolverStatus(ProblemId_ problemId) {
        DefaultSolverJob<Solution_, ProblemId_> solverJob = problemIdToSolverJobMap.get(problemId);
        if (solverJob == null) {
            return SolverStatus.NOT_SOLVING;
        }
        return solverJob.getSolverStatus();
    }

    // TODO Future features
//    @Override
//    public void reloadProblem(ProblemId_ problemId, Supplier<Solution_> problemSupplier) {
//        DefaultSolverJob<Solution_, ProblemId_> solverJob = problemIdToSolverJobMap.get(problemId);
//        if (solverJob == null) {
//            // We cannot distinguish between "already terminated" and "never solved" without causing a memory leak.
//            logger.debug("Ignoring reloadProblem() call because problemId ({}) is not solving.", problemId);
//            return;
//        }
//        solverJob.reloadProblem(problemSupplier);
//    }

    // TODO Future features
//    @Override
//    public void addProblemFactChange(ProblemId_ problemId, ProblemFactChange<Solution_> problemFactChange) {
//        DefaultSolverJob<Solution_, ProblemId_> solverJob = problemIdToSolverJobMap.get(problemId);
//        if (solverJob == null) {
//            // We cannot distinguish between "already terminated" and "never solved" without causing a memory leak.
//            logger.debug("Ignoring addProblemFactChange() call because problemId ({}) is not solving.", problemId);
//            return;
//        }
//        solverJob.addProblemFactChange(problemFactChange);
//    }

    @Override
    public void terminateEarly(ProblemId_ problemId) {
        DefaultSolverJob<Solution_, ProblemId_> solverJob = problemIdToSolverJobMap.get(problemId);
        if (solverJob == null) {
            // We cannot distinguish between "already terminated" and "never solved" without causing a memory leak.
            logger.debug("Ignoring terminateEarly() call because problemId ({}) is not solving.", problemId);
            return;
        }
        solverJob.terminateEarly();
    }

    @Override
    public void updateScore(Solution_ solution) {
        ScoreDirectorFactory<Solution_> scoreDirectorFactory = solverFactory.getScoreDirectorFactory();
        try (ScoreDirector<Solution_> scoreDirector = scoreDirectorFactory.buildScoreDirector()) {
            scoreDirector.setWorkingSolution(solution);
            scoreDirector.calculateScore();
        }
    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }

}
