/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl;

import java.util.concurrent.Callable;

import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.SubSingleStatistic;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class SubSingleBenchmarkRunner<Solution_> implements Callable<SubSingleBenchmarkRunner<Solution_>> {

    public static final String NAME_MDC = "subSingleBenchmark.name";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final SubSingleBenchmarkResult subSingleBenchmarkResult;
    private final SolverConfigContext solverConfigContext;
    private final boolean warmUp;

    private Throwable failureThrowable = null;

    /**
     * @param subSingleBenchmarkResult never null
     */
    public SubSingleBenchmarkRunner(SubSingleBenchmarkResult subSingleBenchmarkResult, boolean warmUp) {
        this(subSingleBenchmarkResult, warmUp, new SolverConfigContext());
    }

    /**
     * @param subSingleBenchmarkResult never null
     * @param solverConfigContext never null
     */
    public SubSingleBenchmarkRunner(SubSingleBenchmarkResult subSingleBenchmarkResult, boolean warmUp,
            SolverConfigContext solverConfigContext) {
        this.subSingleBenchmarkResult = subSingleBenchmarkResult;
        this.warmUp = warmUp;
        this.solverConfigContext = solverConfigContext;
    }

    public SubSingleBenchmarkResult getSubSingleBenchmarkResult() {
        return subSingleBenchmarkResult;
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
        ProblemBenchmarkResult<Solution_> problemBenchmarkResult = subSingleBenchmarkResult.getSingleBenchmarkResult().getProblemBenchmarkResult();
        Solution_ problem = problemBenchmarkResult.readProblem();
        if (!problemBenchmarkResult.getPlannerBenchmarkResult().hasMultipleParallelBenchmarks()) {
            runtime.gc();
            subSingleBenchmarkResult.setUsedMemoryAfterInputSolution(runtime.totalMemory() - runtime.freeMemory());
        }
        logger.trace("Benchmark problem has been read for subSingleBenchmarkResult ({}).",
                subSingleBenchmarkResult);

        SolverConfig solverConfig = subSingleBenchmarkResult.getSingleBenchmarkResult().getSolverBenchmarkResult().getSolverConfig();
        if (subSingleBenchmarkResult.getSingleBenchmarkResult().getSubSingleCount() > 1) {
            solverConfig = new SolverConfig(solverConfig);
            solverConfig.offerRandomSeedFromSubSingleIndex((long) subSingleBenchmarkResult.getSubSingleBenchmarkIndex());
        }
        // Defensive copy of solverConfig for every SingleBenchmarkResult to reset Random, tabu lists, ...
        DefaultSolverFactory<Solution_> solverFactory = new DefaultSolverFactory<>(
                new SolverConfig(solverConfig), solverConfigContext);
        Solver<Solution_> solver = solverFactory.buildSolver();

        for (SubSingleStatistic subSingleStatistic : subSingleBenchmarkResult.getEffectiveSubSingleStatisticMap().values()) {
            subSingleStatistic.open(solver);
            subSingleStatistic.initPointList();
        }

        Solution_ solution = solver.solve(problem);
        long timeMillisSpent = solver.getTimeMillisSpent();

        DefaultSolverScope<Solution_> solverScope = ((DefaultSolver<Solution_>) solver).getSolverScope();
        SolutionDescriptor<Solution_> solutionDescriptor = solverScope.getSolutionDescriptor();
        problemBenchmarkResult.registerScale(solutionDescriptor.getEntityCount(solution),
                solutionDescriptor.getGenuineVariableCount(solution),
                solutionDescriptor.getMaximumValueCount(solution),
                solutionDescriptor.getProblemScale(solution));
        subSingleBenchmarkResult.setScore(solutionDescriptor.getScore(solution));
        subSingleBenchmarkResult.setTimeMillisSpent(timeMillisSpent);
        subSingleBenchmarkResult.setScoreCalculationCount(solverScope.getScoreCalculationCount());

        for (SubSingleStatistic subSingleStatistic : subSingleBenchmarkResult.getEffectiveSubSingleStatisticMap().values()) {
            subSingleStatistic.close(solver);
            subSingleStatistic.hibernatePointList();
        }
        if (!warmUp) {
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
