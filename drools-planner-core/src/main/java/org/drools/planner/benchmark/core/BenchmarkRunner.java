package org.drools.planner.benchmark.core;

import java.io.File;
import java.util.concurrent.Callable;

import org.drools.planner.benchmark.api.ProblemIO;
import org.drools.planner.benchmark.core.statistic.ProblemStatistic;
import org.drools.planner.core.Solver;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolver;
import org.drools.planner.core.solver.DefaultSolverScope;

class BenchmarkRunner implements Callable<Boolean> {

    private final PlannerBenchmarkResult result;
    private final ProblemBenchmark problem;

    public BenchmarkRunner(PlannerBenchmarkResult result, ProblemBenchmark problem) {
        this.result = result;
        this.problem = problem;
    }

    public Boolean call() throws Exception {
        SolverBenchmark solverBenchmark = result.getSolverBenchmark();
        // Intentionally create a fresh solver for every result to reset Random, tabu lists, ...
        Solver solver = solverBenchmark.getSolverConfig().buildSolver();
        for (ProblemStatistic statistic : problem.getProblemStatisticList()) {
            statistic.addListener(solver, solverBenchmark.getName());
        }

        solver.setPlanningProblem(problem.readPlanningProblem());
        solver.solve();
        Solution outputSolution = solver.getBestSolution();

        result.setTimeMillisSpend(solver.getTimeMillisSpend());
        DefaultSolverScope solverScope = ((DefaultSolver) solver).getSolverScope();
        result.setCalculateCount(solverScope.getCalculateCount());
        result.setScore(outputSolution.getScore());
        SolutionDescriptor solutionDescriptor = ((DefaultSolver) solver).getSolutionDescriptor();
        result.setPlanningEntityCount(solutionDescriptor.getPlanningEntityCount(outputSolution));
        result.setProblemScale(solutionDescriptor.getProblemScale(outputSolution));
        for (ProblemStatistic statistic : problem.getProblemStatisticList()) {
            statistic.removeListener(solver, solverBenchmark.getName());
        }
        writeSolution(result, outputSolution);
        return true;
    }

    private void writeSolution(PlannerBenchmarkResult result, Solution outputSolution) {
        ProblemIO problemIo = problem.getProblemIO();
        String solverBenchmarkName = result.getSolverBenchmark().getName()
                .replaceAll(" ", "_").replaceAll("[^\\w\\d_\\-]", "");
        String filename = problem.getName() + "_" + solverBenchmarkName + "." + problemIo.getFileExtension();
        File outputSolutionFile = new File(problem.getOutputSolutionFilesDirectory(), filename);
        problemIo.write(outputSolution, outputSolutionFile);
    }

}
