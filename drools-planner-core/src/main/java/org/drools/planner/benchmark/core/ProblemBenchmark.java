/*
 * Copyright 2011 JBoss Inc
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

import java.io.File;
import java.util.List;

import org.drools.planner.benchmark.core.statistic.ProblemStatistic;
import org.drools.planner.config.termination.TerminationConfig;
import org.drools.planner.core.Solver;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolver;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * Represents one problem instance (a data set) benchmarked on multiple solvers.
 */
public class ProblemBenchmark {

    private String name = null;

    private ProblemIO problemIO = null;
    private File inputSolutionFile = null;
    private File outputSolutionFilesDirectory = null;

    private List<ProblemStatistic> problemStatisticList = null;

    private List<PlannerBenchmarkResult> plannerBenchmarkResultList = null;
    
    private PlannerBenchmarkResult winningPlannerBenchmarkResult = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProblemIO getProblemIO() {
        return problemIO;
    }

    public void setProblemIO(ProblemIO problemIO) {
        this.problemIO = problemIO;
    }

    public File getInputSolutionFile() {
        return inputSolutionFile;
    }

    public void setInputSolutionFile(File inputSolutionFile) {
        this.inputSolutionFile = inputSolutionFile;
    }

    public File getOutputSolutionFilesDirectory() {
        return outputSolutionFilesDirectory;
    }

    public void setOutputSolutionFilesDirectory(File outputSolutionFilesDirectory) {
        this.outputSolutionFilesDirectory = outputSolutionFilesDirectory;
    }

    public List<ProblemStatistic> getProblemStatisticList() {
        return problemStatisticList;
    }

    public void setProblemStatisticList(List<ProblemStatistic> problemStatisticList) {
        this.problemStatisticList = problemStatisticList;
    }

    public List<PlannerBenchmarkResult> getPlannerBenchmarkResultList() {
        return plannerBenchmarkResultList;
    }

    public void setPlannerBenchmarkResultList(List<PlannerBenchmarkResult> plannerBenchmarkResultList) {
        this.plannerBenchmarkResultList = plannerBenchmarkResultList;
    }

    public PlannerBenchmarkResult getWinningPlannerBenchmarkResult() {
        return winningPlannerBenchmarkResult;
    }

    public void setWinningPlannerBenchmarkResult(PlannerBenchmarkResult winningPlannerBenchmarkResult) {
        this.winningPlannerBenchmarkResult = winningPlannerBenchmarkResult;
    }

    // ************************************************************************
    // Benchmark methods
    // ************************************************************************

    public void benchmarkingStarted() {
    }

    public void benchmark() {
        for (PlannerBenchmarkResult result : plannerBenchmarkResultList) {
            SolverBenchmark solverBenchmark = result.getSolverBenchmark();
            // Intentionally create a fresh solver for every result to reset Random, tabu lists, ...
            Solver solver = solverBenchmark.getSolverConfig().buildSolver();
            for (ProblemStatistic statistic : problemStatisticList) {
                statistic.addListener(solver, solverBenchmark.getName());
            }

            solver.setPlanningProblem(readPlanningProblem());
            solver.solve();
            Solution outputSolution = solver.getBestSolution();

            result.setTimeMillisSpend(solver.getTimeMillisSpend());
            DefaultSolverScope solverScope = ((DefaultSolver) solver).getSolverScope();
            result.setCalculateCount(solverScope.getCalculateCount());
            result.setScore(outputSolution.getScore());
            SolutionDescriptor solutionDescriptor = ((DefaultSolver) solver).getSolutionDescriptor();
            result.setPlanningEntityCount(solutionDescriptor.getPlanningEntityCount(outputSolution));
            result.setProblemScale(solutionDescriptor.getProblemScale(outputSolution));
            for (ProblemStatistic statistic : problemStatisticList) {
                statistic.removeListener(solver, solverBenchmark.getName());
            }
            writeSolution(result, outputSolution);
        }
    }

    public long warmUp(long startingTimeMillis, long warmUpTimeMillisSpend, long timeLeft) {
        for (PlannerBenchmarkResult result : plannerBenchmarkResultList) {
            SolverBenchmark solverBenchmark = result.getSolverBenchmark();
            TerminationConfig originalTerminationConfig = solverBenchmark.getSolverConfig().getTerminationConfig();
            TerminationConfig tmpTerminationConfig = originalTerminationConfig.clone();
            tmpTerminationConfig.shortenMaximumTimeMillisSpendTotal(timeLeft);
            solverBenchmark.getSolverConfig().setTerminationConfig(tmpTerminationConfig);

            Solver solver = solverBenchmark.getSolverConfig().buildSolver();
            solver.setPlanningProblem(readPlanningProblem());
            solver.solve();

            solverBenchmark.getSolverConfig().setTerminationConfig(originalTerminationConfig);
            long timeSpend = System.currentTimeMillis() - startingTimeMillis;
            timeLeft = warmUpTimeMillisSpend - timeSpend;
            if (timeLeft <= 0L) {
                return timeLeft;
            }
        }
        return timeLeft;
    }

    public Solution readPlanningProblem() {
        return problemIO.read(inputSolutionFile);
    }

    private void writeSolution(PlannerBenchmarkResult result, Solution outputSolution) {
        String solverBenchmarkName = result.getSolverBenchmark().getName()
                .replaceAll(" ", "_").replaceAll("[^\\w\\d_\\-]", "");
        String filename = name + "_" + solverBenchmarkName + "." + problemIO.getFileExtension();
        File outputSolutionFile = new File(outputSolutionFilesDirectory, filename);
        problemIO.write(outputSolution, outputSolutionFile);
    }

    public void benchmarkingEnded() {
        determineWinningResult();
        determineWinningResultScoreDifference();
    }

    private void determineWinningResult() {
        winningPlannerBenchmarkResult = null;
        for (PlannerBenchmarkResult result : plannerBenchmarkResultList) {
            if (winningPlannerBenchmarkResult == null
                    || result.getScore().compareTo(winningPlannerBenchmarkResult.getScore()) > 0) {
                winningPlannerBenchmarkResult = result;
            }
        }
    }

    private void determineWinningResultScoreDifference() {
        for (PlannerBenchmarkResult result : plannerBenchmarkResultList) {
            result.setWinningScoreDifference(result.getScore().subtract(winningPlannerBenchmarkResult.getScore()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ProblemBenchmark) {
            ProblemBenchmark other = (ProblemBenchmark) o;
            return inputSolutionFile.equals(other.getInputSolutionFile());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return inputSolutionFile.hashCode();
    }

}
