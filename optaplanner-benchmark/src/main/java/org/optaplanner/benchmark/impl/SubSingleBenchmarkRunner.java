/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.benchmark.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.StatisticRegistry;
import org.optaplanner.benchmark.impl.statistic.SubSingleStatistic;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolutionUpdatePolicy;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;

public class SubSingleBenchmarkRunner<Solution_> implements Callable<SubSingleBenchmarkRunner<Solution_>> {

    public static final String NAME_MDC = "subSingleBenchmark.name";

    private static final Logger LOGGER = LoggerFactory.getLogger(SubSingleBenchmarkRunner.class);

    private final SubSingleBenchmarkResult subSingleBenchmarkResult;
    private final boolean warmUp;

    private Long randomSeed = null;
    private Throwable failureThrowable = null;

    /**
     * @param subSingleBenchmarkResult never null
     */
    public SubSingleBenchmarkRunner(SubSingleBenchmarkResult subSingleBenchmarkResult, boolean warmUp) {
        this.subSingleBenchmarkResult = subSingleBenchmarkResult;
        this.warmUp = warmUp;
    }

    public SubSingleBenchmarkResult getSubSingleBenchmarkResult() {
        return subSingleBenchmarkResult;
    }

    public Long getRandomSeed() {
        return randomSeed;
    }

    public Throwable getFailureThrowable() {
        return failureThrowable;
    }

    public void setFailureThrowable(Throwable failureThrowable) {
        this.failureThrowable = failureThrowable;
    }

    // ************************************************************************
    // Benchmark methods
    // ************************************************************************

    @Override
    public SubSingleBenchmarkRunner<Solution_> call() {
        MDC.put(NAME_MDC, subSingleBenchmarkResult.getName());
        Runtime runtime = Runtime.getRuntime();
        SingleBenchmarkResult singleBenchmarkResult = subSingleBenchmarkResult.getSingleBenchmarkResult();
        ProblemBenchmarkResult<Solution_> problemBenchmarkResult = singleBenchmarkResult
                .getProblemBenchmarkResult();
        Solution_ problem = problemBenchmarkResult.readProblem();
        if (!problemBenchmarkResult.getPlannerBenchmarkResult().hasMultipleParallelBenchmarks()) {
            runtime.gc();
            subSingleBenchmarkResult.setUsedMemoryAfterInputSolution(runtime.totalMemory() - runtime.freeMemory());
        }
        LOGGER.trace("Benchmark problem has been read for subSingleBenchmarkResult ({}).",
                subSingleBenchmarkResult);

        SolverConfig solverConfig = singleBenchmarkResult.getSolverBenchmarkResult()
                .getSolverConfig();
        if (singleBenchmarkResult.getSubSingleCount() > 1) {
            solverConfig = new SolverConfig(solverConfig);
            solverConfig.offerRandomSeedFromSubSingleIndex(subSingleBenchmarkResult.getSubSingleBenchmarkIndex());
        }
        Map<String, String> subSingleBenchmarkTagMap = new HashMap<>();
        String runId = UUID.randomUUID().toString();
        subSingleBenchmarkTagMap.put("optaplanner.benchmark.run", runId);
        solverConfig = new SolverConfig(solverConfig);
        randomSeed = solverConfig.getRandomSeed();
        // Defensive copy of solverConfig for every SingleBenchmarkResult to reset Random, tabu lists, ...
        DefaultSolverFactory<Solution_> solverFactory = new DefaultSolverFactory<>(new SolverConfig(solverConfig));
        DefaultSolver<Solution_> solver = (DefaultSolver<Solution_>) solverFactory.buildSolver();
        solver.setMonitorTagMap(subSingleBenchmarkTagMap);
        StatisticRegistry<Solution_> statisticRegistry = new StatisticRegistry<>(solver);
        Metrics.addRegistry(statisticRegistry);
        solver.addPhaseLifecycleListener(statisticRegistry);

        Tags runTag = Tags.of("optaplanner.benchmark.run", runId);
        for (SubSingleStatistic<Solution_, ?> subSingleStatistic : subSingleBenchmarkResult.getEffectiveSubSingleStatisticMap()
                .values()) {
            subSingleStatistic.open(statisticRegistry, runTag, solver);
            subSingleStatistic.initPointList();
        }
        Solution_ solution = solver.solve(problem);

        solver.removePhaseLifecycleListener(statisticRegistry);
        Metrics.removeRegistry(statisticRegistry);
        long timeMillisSpent = solver.getTimeMillisSpent();

        for (SubSingleStatistic<Solution_, ?> subSingleStatistic : subSingleBenchmarkResult.getEffectiveSubSingleStatisticMap()
                .values()) {
            subSingleStatistic.close(statisticRegistry, runTag, solver);
            subSingleStatistic.hibernatePointList();
        }
        if (!warmUp) {
            SolverScope<Solution_> solverScope = solver.getSolverScope();
            SolutionDescriptor<Solution_> solutionDescriptor = solverScope.getSolutionDescriptor();
            problemBenchmarkResult.registerScale(solutionDescriptor.getEntityCount(solution),
                    solutionDescriptor.getGenuineVariableCount(solution),
                    solutionDescriptor.getMaximumValueCount(solution),
                    solutionDescriptor.getProblemScale(solution));
            subSingleBenchmarkResult.setScore(solutionDescriptor.getScore(solution));
            subSingleBenchmarkResult.setTimeMillisSpent(timeMillisSpent);
            subSingleBenchmarkResult.setScoreCalculationCount(solverScope.getScoreCalculationCount());

            SolutionManager<Solution_, ?> solutionManager = SolutionManager.create(solverFactory);
            boolean isConstraintMatchEnabled = solver.getSolverScope().getScoreDirector().isConstraintMatchEnabled();
            if (isConstraintMatchEnabled) { // Easy calculator fails otherwise.
                ScoreExplanation<Solution_, ?> scoreExplanation =
                        solutionManager.explain(solution, SolutionUpdatePolicy.NO_UPDATE);
                subSingleBenchmarkResult.setScoreExplanationSummary(scoreExplanation.getSummary());
            }

            problemBenchmarkResult.writeSolution(subSingleBenchmarkResult, solution);
        }
        MDC.remove(NAME_MDC);
        return this;
    }

    public String getName() {
        return subSingleBenchmarkResult.getName();
    }

    @Override
    public String toString() {
        return subSingleBenchmarkResult.toString();
    }

}
