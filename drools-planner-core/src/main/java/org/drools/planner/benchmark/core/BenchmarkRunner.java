/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.benchmark.core;

import java.util.concurrent.Callable;

import org.drools.planner.benchmark.core.statistic.ProblemStatistic;
import org.drools.planner.core.Solver;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolver;
import org.drools.planner.core.solver.DefaultSolverScope;

public class BenchmarkRunner implements Callable<Boolean> {

    private final PlannerBenchmarkResult benchmarkResult;
    private final ProblemBenchmark problemBenchmark;

    public BenchmarkRunner(PlannerBenchmarkResult benchmarkResult, ProblemBenchmark problemBenchmark) {
        this.benchmarkResult = benchmarkResult;
        this.problemBenchmark = problemBenchmark;
    }

    public Boolean call() throws Exception {
        SolverBenchmark solverBenchmark = benchmarkResult.getSolverBenchmark();
        // Intentionally create a fresh solver for every result to reset Random, tabu lists, ...
        Solver solver = solverBenchmark.getSolverConfig().buildSolver();
        for (ProblemStatistic statistic : problemBenchmark.getProblemStatisticList()) {
            statistic.addListener(solver, solverBenchmark.getName());
        }

        solver.setPlanningProblem(problemBenchmark.readPlanningProblem());
        solver.solve();
        Solution outputSolution = solver.getBestSolution();

        benchmarkResult.setTimeMillisSpend(solver.getTimeMillisSpend());
        DefaultSolverScope solverScope = ((DefaultSolver) solver).getSolverScope();
        benchmarkResult.setCalculateCount(solverScope.getCalculateCount());
        benchmarkResult.setScore(outputSolution.getScore());
        SolutionDescriptor solutionDescriptor = ((DefaultSolver) solver).getSolutionDescriptor();
        benchmarkResult.setPlanningEntityCount(solutionDescriptor.getPlanningEntityCount(outputSolution));
        benchmarkResult.setProblemScale(solutionDescriptor.getProblemScale(outputSolution));
        for (ProblemStatistic statistic : problemBenchmark.getProblemStatisticList()) {
            statistic.removeListener(solver, solverBenchmark.getName());
        }
        problemBenchmark.writeSolution(benchmarkResult, outputSolution);
        return true;
    }

}
