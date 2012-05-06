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

import org.drools.planner.benchmark.api.ProblemIO;
import org.drools.planner.benchmark.core.statistic.ProblemStatistic;
import org.drools.planner.config.termination.TerminationConfig;
import org.drools.planner.core.Solver;
import org.drools.planner.core.solution.Solution;

/**
 * Represents 1 problem instance (data set) benchmarked on multiple {@link Solver} configurations.
 */
public class ProblemBenchmark {

    private String name = null;

    private ProblemIO problemIO = null;
    private File inputSolutionFile = null;
    private File outputSolutionFilesDirectory = null;

    private List<ProblemStatistic> problemStatisticList = null;

    private List<SingleBenchmark> singleBenchmarkList = null;

    private Integer failureCount = null;
    private SingleBenchmark winningSingleBenchmark = null;

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

    public List<SingleBenchmark> getSingleBenchmarkList() {
        return singleBenchmarkList;
    }

    public void setSingleBenchmarkList(List<SingleBenchmark> singleBenchmarkList) {
        this.singleBenchmarkList = singleBenchmarkList;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public SingleBenchmark getWinningSingleBenchmark() {
        return winningSingleBenchmark;
    }

    // ************************************************************************
    // Benchmark methods
    // ************************************************************************

    public void benchmarkingStarted() {
    }

    public long warmUp(long startingTimeMillis, long warmUpTimeMillisSpend, long timeLeft) {
        for (SingleBenchmark singleBenchmark : singleBenchmarkList) {
            SolverBenchmark solverBenchmark = singleBenchmark.getSolverBenchmark();
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

    public void writeSolution(SingleBenchmark singleBenchmark, Solution outputSolution) {
        String filename = singleBenchmark.getName() + "." + problemIO.getFileExtension();
        File outputSolutionFile = new File(outputSolutionFilesDirectory, filename);
        problemIO.write(outputSolution, outputSolutionFile);
    }

    public void benchmarkingEnded() {
        determineWinningSingleBenchmark();
        determineWinningSingleBenchmarkScoreDifference();
    }

    private void determineWinningSingleBenchmark() {
        failureCount = 0;
        winningSingleBenchmark = null;
        for (SingleBenchmark singleBenchmark : singleBenchmarkList) {
            if (singleBenchmark.isFailure()) {
                failureCount++;
            } else {
                if (winningSingleBenchmark == null
                        || singleBenchmark.getScore().compareTo(winningSingleBenchmark.getScore()) > 0) {
                    winningSingleBenchmark = singleBenchmark;
                }
            }
        }
    }

    private void determineWinningSingleBenchmarkScoreDifference() {
        for (SingleBenchmark singleBenchmark : singleBenchmarkList) {
            if (singleBenchmark.isFailure()) {
                continue;
            }
            singleBenchmark.setWinningScoreDifference(singleBenchmark.getScore().subtract(winningSingleBenchmark.getScore()));
        }
    }

    public boolean hasFailure() {
        return failureCount > 0;
    }

    public boolean hasAnySuccess() {
        return singleBenchmarkList.size() - failureCount > 0;
    }

    public boolean hasAnyProblemStatistic() {
        return problemStatisticList.size() > 0;
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
