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

package org.optaplanner.benchmark.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.benchmark.impl.measurement.ScoreDifferencePercentage;
import org.optaplanner.benchmark.impl.ranking.SingleBenchmarkRankingComparator;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.core.config.termination.TerminationConfig;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.solution.ProblemIO;
import org.optaplanner.core.impl.solution.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents 1 problem instance (data set) benchmarked on multiple {@link Solver} configurations.
 */
public class ProblemBenchmark {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final DefaultPlannerBenchmark plannerBenchmark;

    private String name = null;

    private ProblemIO problemIO = null;
    private boolean writeOutputSolutionEnabled = false;
    private File inputSolutionFile = null;
    private File problemReportDirectory = null;

    private List<ProblemStatistic> problemStatisticList = null;

    private List<SingleBenchmark> singleBenchmarkList = null;

    private Long problemScale = null;
    private Long averageUsedMemoryAfterInputSolution = null;
    private Integer failureCount = null;
    private SingleBenchmark winningSingleBenchmark = null;
    private SingleBenchmark worstSingleBenchmark = null;

    public ProblemBenchmark(DefaultPlannerBenchmark plannerBenchmark) {
        this.plannerBenchmark = plannerBenchmark;
    }

    public DefaultPlannerBenchmark getPlannerBenchmark() {
        return plannerBenchmark;
    }

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

    public boolean isWriteOutputSolutionEnabled() {
        return writeOutputSolutionEnabled;
    }

    public void setWriteOutputSolutionEnabled(boolean writeOutputSolutionEnabled) {
        this.writeOutputSolutionEnabled = writeOutputSolutionEnabled;
    }

    public File getInputSolutionFile() {
        return inputSolutionFile;
    }

    public File getProblemReportDirectory() {
        return problemReportDirectory;
    }

    public void setInputSolutionFile(File inputSolutionFile) {
        this.inputSolutionFile = inputSolutionFile;
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

    public Long getProblemScale() {
        return problemScale;
    }

    public Long getAverageUsedMemoryAfterInputSolution() {
        return averageUsedMemoryAfterInputSolution;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public SingleBenchmark getWinningSingleBenchmark() {
        return winningSingleBenchmark;
    }

    public SingleBenchmark getWorstSingleBenchmark() {
        return worstSingleBenchmark;
    }

    // ************************************************************************
    // Benchmark methods
    // ************************************************************************

    public void benchmarkingStarted() {
        problemReportDirectory = new File(plannerBenchmark.getBenchmarkReportDirectory(), name);
        problemReportDirectory.mkdirs();
        problemScale = null;
    }

    public long warmUp(long startingTimeMillis, long warmUpTimeMillisSpend, long timeLeft) {
        for (SingleBenchmark singleBenchmark : singleBenchmarkList) {
            SolverBenchmark solverBenchmark = singleBenchmark.getSolverBenchmark();
            TerminationConfig originalTerminationConfig = solverBenchmark.getSolverConfig().getTerminationConfig();
            TerminationConfig tmpTerminationConfig = originalTerminationConfig == null
                    ? new TerminationConfig() : originalTerminationConfig.clone();
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

    public void writeOutputSolution(SingleBenchmark singleBenchmark, Solution outputSolution) {
        if (!writeOutputSolutionEnabled) {
            return;
        }
        String filename = singleBenchmark.getName() + "." + problemIO.getFileExtension();
        File outputSolutionFile = new File(problemReportDirectory, filename);
        problemIO.write(outputSolution, outputSolutionFile);
    }

    public void benchmarkingEnded() {
        determineTotalsAndAveragesAndRanking();
        determineWinningScoreDifference();
    }

    private void determineTotalsAndAveragesAndRanking() {
        failureCount = 0;
        long totalUsedMemoryAfterInputSolution = 0L;
        int usedMemoryAfterInputSolutionCount = 0;
        List<SingleBenchmark> successSingleBenchmarkList = new ArrayList<SingleBenchmark>(singleBenchmarkList);
        // Do not rank a SingleBenchmark that has a failure
        for (Iterator<SingleBenchmark> it = successSingleBenchmarkList.iterator(); it.hasNext(); ) {
            SingleBenchmark singleBenchmark = it.next();
            if (singleBenchmark.isFailure()) {
                failureCount++;
                it.remove();
            } else {
                if (singleBenchmark.getUsedMemoryAfterInputSolution() != null) {
                    totalUsedMemoryAfterInputSolution += singleBenchmark.getUsedMemoryAfterInputSolution();
                    usedMemoryAfterInputSolutionCount++;
                }
            }
        }
        if (usedMemoryAfterInputSolutionCount > 0) {
            averageUsedMemoryAfterInputSolution = totalUsedMemoryAfterInputSolution
                    / (long) usedMemoryAfterInputSolutionCount;
        }
        determineRanking(successSingleBenchmarkList);
    }

    private void determineRanking(List<SingleBenchmark> rankedSingleBenchmarkList) {
        Comparator singleBenchmarkRankingComparator = new SingleBenchmarkRankingComparator();
        Collections.sort(rankedSingleBenchmarkList, Collections.reverseOrder(singleBenchmarkRankingComparator));
        int ranking = 0;
        SingleBenchmark previousSingleBenchmark = null;
        int previousSameRankingCount = 0;
        for (SingleBenchmark singleBenchmark : rankedSingleBenchmarkList) {
            if (previousSingleBenchmark != null
                    && singleBenchmarkRankingComparator.compare(previousSingleBenchmark, singleBenchmark) != 0) {
                ranking += previousSameRankingCount;
                previousSameRankingCount = 0;
            }
            singleBenchmark.setRanking(ranking);
            previousSingleBenchmark = singleBenchmark;
            previousSameRankingCount++;
        }
        winningSingleBenchmark = rankedSingleBenchmarkList.isEmpty() ? null : rankedSingleBenchmarkList.get(0);
        worstSingleBenchmark = rankedSingleBenchmarkList.isEmpty() ? null
                : rankedSingleBenchmarkList.get(rankedSingleBenchmarkList.size() - 1);
    }

    private void determineWinningScoreDifference() {
        for (SingleBenchmark singleBenchmark : singleBenchmarkList) {
            if (singleBenchmark.isFailure()) {
                continue;
            }
            singleBenchmark.setWinningScoreDifference(
                    singleBenchmark.getScore().subtract(winningSingleBenchmark.getScore()));
            singleBenchmark.setWorstScoreDifferencePercentage(
                    ScoreDifferencePercentage.calculateScoreDifferencePercentage(
                            worstSingleBenchmark.getScore(), singleBenchmark.getScore()));
        }
    }

    public boolean hasAnyFailure() {
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

    /**
     * HACK to avoid loading the planningProblem just to extract it's problemScale.
     * Called multiple times, for every {@link SingleBenchmark} of this {@link ProblemBenchmark}.
     *
     * @param registeringProblemScale >= 0
     */
    public void registerProblemScale(long registeringProblemScale) {
        if (problemScale == null) {
            problemScale = registeringProblemScale;
        } else if (problemScale.longValue() != registeringProblemScale) {
            logger.warn("The problemBenchmark ({}) has different problemScale values ([{},{}]).",
                    getName(), problemScale, registeringProblemScale);
            // The problemScale is not unknown (null), but known to be ambiguous
            problemScale = -1L;
        }
    }

}
