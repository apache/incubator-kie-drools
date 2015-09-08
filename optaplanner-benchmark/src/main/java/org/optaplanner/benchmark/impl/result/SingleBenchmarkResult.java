/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.benchmark.impl.result;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.benchmark.impl.measurement.ScoreDifferencePercentage;
import org.optaplanner.benchmark.impl.ranking.SubSingleBenchmarkRankingComparator;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.statistic.PureSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticType;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.score.ScoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents 1 benchmark for 1 {@link Solver} configuration for 1 problem instance (data set).
 */
@XStreamAlias("singleBenchmarkResult")
public class SingleBenchmarkResult implements SolverProblemBenchmarkResult {

    protected static final transient Logger logger = LoggerFactory.getLogger(SingleBenchmarkResult.class);

    @XStreamOmitField // Bi-directional relationship restored through BenchmarkResultIO
    private SolverBenchmarkResult solverBenchmarkResult;
    @XStreamOmitField // Bi-directional relationship restored through BenchmarkResultIO
    private ProblemBenchmarkResult problemBenchmarkResult;

    @XStreamImplicit(itemFieldName = "subSingleBenchmarkResult")
    private List<SubSingleBenchmarkResult> subSingleBenchmarkResultList = null;

    @XStreamImplicit()
    private List<PureSingleStatistic> pureSingleStatisticList = null;

    @XStreamOmitField // Lazily restored when read through ProblemStatistic and CSV files
    private Map<StatisticType, SingleStatistic> effectiveSingleStatisticMap;

    private Long usedMemoryAfterInputSolution = null;

    private Boolean succeeded = null;
    private Integer uninitializedVariableCount = null;
    private Score score = null;
    private long timeMillisSpent = -1L;
    private long calculateCount = -1L;

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    // Compared to winningSingleBenchmarkResult in the same ProblemBenchmarkResult (which might not be the overall favorite)
    private Score winningScoreDifference = null;
    private ScoreDifferencePercentage worstScoreDifferencePercentage = null;

    // Ranking starts from 0
    private Integer ranking = null;

    public SingleBenchmarkResult(SolverBenchmarkResult solverBenchmarkResult, ProblemBenchmarkResult problemBenchmarkResult) {
        this.solverBenchmarkResult = solverBenchmarkResult;
        this.problemBenchmarkResult = problemBenchmarkResult;
    }

    public List<PureSingleStatistic> getPureSingleStatisticList() {
        return pureSingleStatisticList;
    }

    public void setPureSingleStatisticList(List<PureSingleStatistic> pureSingleStatisticList) {
        this.pureSingleStatisticList = pureSingleStatisticList;
    }

    public void initSingleStatisticMap() {
        effectiveSingleStatisticMap = new HashMap<StatisticType, SingleStatistic>(pureSingleStatisticList.size()
                + problemBenchmarkResult.getProblemStatisticList().size());
    }

    @Override
    public SolverBenchmarkResult getSolverBenchmarkResult() {
        return solverBenchmarkResult;
    }

    public void setSolverBenchmarkResult(SolverBenchmarkResult solverBenchmarkResult) {
        this.solverBenchmarkResult = solverBenchmarkResult;
    }

    @Override
    public ProblemBenchmarkResult getProblemBenchmarkResult() {
        return problemBenchmarkResult;
    }

    public void setProblemBenchmarkResult(ProblemBenchmarkResult problemBenchmarkResult) {
        this.problemBenchmarkResult = problemBenchmarkResult;
    }

    public List<SubSingleBenchmarkResult> getSubSingleBenchmarkResultList() {
        return subSingleBenchmarkResultList;
    }

    public void setSubSingleBenchmarkResultList(List<SubSingleBenchmarkResult> subSingleBenchmarkResultList) {
        this.subSingleBenchmarkResultList = subSingleBenchmarkResultList;
    }

    public Map<StatisticType, SingleStatistic> getEffectiveSingleStatisticMap() {
        return effectiveSingleStatisticMap;
    }

    /**
     * @return null if {@link PlannerBenchmarkResult#hasMultipleParallelBenchmarks()} return true
     */
    public Long getUsedMemoryAfterInputSolution() {
        return usedMemoryAfterInputSolution;
    }

    public void setUsedMemoryAfterInputSolution(Long usedMemoryAfterInputSolution) {
        this.usedMemoryAfterInputSolution = usedMemoryAfterInputSolution;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(Boolean succeeded) {
        this.succeeded = succeeded;
    }

    public Integer getUninitializedVariableCount() {
        return uninitializedVariableCount;
    }

    public void setUninitializedVariableCount(Integer uninitializedVariableCount) {
        this.uninitializedVariableCount = uninitializedVariableCount;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public void setTimeMillisSpent(long timeMillisSpent) {
        this.timeMillisSpent = timeMillisSpent;
    }

    public long getCalculateCount() {
        return calculateCount;
    }

    public void setCalculateCount(long calculateCount) {
        this.calculateCount = calculateCount;
    }

    public Score getWinningScoreDifference() {
        return winningScoreDifference;
    }

    public void setWinningScoreDifference(Score winningScoreDifference) {
        this.winningScoreDifference = winningScoreDifference;
    }

    public ScoreDifferencePercentage getWorstScoreDifferencePercentage() {
        return worstScoreDifferencePercentage;
    }

    public void setWorstScoreDifferencePercentage(ScoreDifferencePercentage worstScoreDifferencePercentage) {
        this.worstScoreDifferencePercentage = worstScoreDifferencePercentage;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    // ************************************************************************
    // Smart getters
    // ************************************************************************

    /**
     * @return never null, filename safe
     */
    public String getName() {
        return problemBenchmarkResult.getName() + "_" + solverBenchmarkResult.getName();
    }

    public File getBenchmarkReportDirectory() {
        return problemBenchmarkResult.getBenchmarkReportDirectory();
    }

    public boolean isSuccess() {
        return succeeded != null && succeeded.booleanValue();
    }

    public boolean isInitialized() {
        return uninitializedVariableCount != null && uninitializedVariableCount == 0;
    }

    public boolean isFailure() {
        return succeeded != null && !succeeded.booleanValue();
    }

    public boolean isScoreFeasible() {
        if (score instanceof FeasibilityScore) {
            return ((FeasibilityScore) score).isFeasible();
        } else {
            return true;
        }
    }

    public Long getAverageCalculateCountPerSecond() {
        long timeMillisSpent = this.timeMillisSpent;
        if (timeMillisSpent == 0L) {
            // Avoid divide by zero exception on a fast CPU
            timeMillisSpent = 1L;
        }
        return calculateCount * 1000L / timeMillisSpent;
    }

    public boolean isWinner() {
        return ranking != null && ranking.intValue() == 0;
    }

    public SingleStatistic getSingleStatistic(StatisticType statisticType) {
        return effectiveSingleStatisticMap.get(statisticType);
    }

    public String getScoreWithUninitializedPrefix() {
        return ScoreUtils.getScoreWithUninitializedPrefix(uninitializedVariableCount, score);
    }

    // ************************************************************************
    // Accumulate methods
    // ************************************************************************

    public String getReportDirectoryPath() {
        return solverBenchmarkResult.getName();
    }

    public File getReportDirectory() {
        return new File(problemBenchmarkResult.getProblemReportDirectory(), getReportDirectoryPath());
    }

    public void makeDirs() {
        File singleReportDirectory = getReportDirectory();
        singleReportDirectory.mkdirs();
        for (SubSingleBenchmarkResult subSingleBenchmarkResult : subSingleBenchmarkResultList) {
            subSingleBenchmarkResult.makeDirs();
        }
    }

    public void accumulateResults(BenchmarkReport benchmarkReport) { // OOO: accumulate according to merge strategy: average, worst case, propagate failure
        determineTotalsAndAveragesAndRanking();
        SubSingleBenchmarkResult[] subSingleBenchmarkResults = new SubSingleBenchmarkResult[subSingleBenchmarkResultList.size()];
        SubSingleBenchmarkResult median = ObjectUtils.median(new SubSingleBenchmarkRankingComparator(), subSingleBenchmarkResultList.toArray(subSingleBenchmarkResults)); // OOO: Use ranking
        this.pureSingleStatisticList = median.getSubPureSingleStatisticList();
        this.effectiveSingleStatisticMap = median.getEffectiveSingleStatisticMap();
        this.usedMemoryAfterInputSolution = median.getUsedMemoryAfterInputSolution();
        this.succeeded = median.getSucceeded();
        this.uninitializedVariableCount = median.getUninitializedVariableCount();
        this.score = median.getScore();
        this.timeMillisSpent = median.getTimeMillisSpent();
        this.calculateCount = median.getCalculateCount();
        this.winningScoreDifference = median.getWinningScoreDifference();
        this.worstScoreDifferencePercentage = median.getWorstScoreDifferencePercentage();

        for (SingleStatistic singleStatistic : median.getEffectiveSingleStatisticMap().values()) { // copy to parent dir
            singleStatistic.unhibernatePointList();
            singleStatistic.setSolverProblemBenchmarkResult(this);
            singleStatistic.hibernatePointList();
        }
    }

    private void determineTotalsAndAveragesAndRanking() {
        int failureCount = 0;
        long totalUsedMemoryAfterInputSolution = 0L;
        int usedMemoryAfterInputSolutionCount = 0;
        List<SubSingleBenchmarkResult> successResultList = new ArrayList<SubSingleBenchmarkResult>(subSingleBenchmarkResultList);
        // Do not rank a SubSingleBenchmarkResult that has a failure
        for (Iterator<SubSingleBenchmarkResult> it = successResultList.iterator(); it.hasNext(); ) {
            SubSingleBenchmarkResult subSingleBenchmarkResult = it.next();
            if (subSingleBenchmarkResult.isFailure()) {
                failureCount++;
                it.remove();
            }
        }
        determineRanking(successResultList);
    }

    private void determineRanking(List<SubSingleBenchmarkResult> rankedSubSingleBenchmarkResultList) {
        Comparator subSingleBenchmarkRankingComparator = new SubSingleBenchmarkRankingComparator();
        Collections.sort(rankedSubSingleBenchmarkResultList, Collections.reverseOrder(subSingleBenchmarkRankingComparator));
        int ranking = 0;
        SubSingleBenchmarkResult previousSubSingleBenchmarkResult = null;
        int previousSameRankingCount = 0;
        for (SubSingleBenchmarkResult subSingleBenchmarkResult : rankedSubSingleBenchmarkResultList) {
            if (previousSubSingleBenchmarkResult != null
                    && subSingleBenchmarkRankingComparator.compare(previousSubSingleBenchmarkResult, subSingleBenchmarkResult) != 0) {
                ranking += previousSameRankingCount;
                previousSameRankingCount = 0;
            }
            subSingleBenchmarkResult.setRanking(ranking);
            previousSubSingleBenchmarkResult = subSingleBenchmarkResult;
            previousSameRankingCount++;
        }
    }

    // ************************************************************************
    // Merger methods
    // ************************************************************************


    protected static SingleBenchmarkResult createMerge(SolverBenchmarkResult solverBenchmarkResult,
            ProblemBenchmarkResult problemBenchmarkResult, SingleBenchmarkResult oldResult) {
        SingleBenchmarkResult newResult = new SingleBenchmarkResult(solverBenchmarkResult, problemBenchmarkResult);
        newResult.pureSingleStatisticList = new ArrayList<PureSingleStatistic>(oldResult.pureSingleStatisticList.size());
        for (PureSingleStatistic oldSingleStatistic : oldResult.pureSingleStatisticList) {
            newResult.pureSingleStatisticList.add(
                    oldSingleStatistic.getStatisticType().buildPureSingleStatistic(newResult));
        }

        newResult.initSingleStatisticMap();
        for (SingleStatistic singleStatistic : newResult.effectiveSingleStatisticMap.values()) {
            SingleStatistic oldSingleStatistic = oldResult.getSingleStatistic(singleStatistic.getStatisticType());
            if (!oldSingleStatistic.getCsvFile().exists()) {
                if (oldResult.isFailure()) {
                    singleStatistic.initPointList();
                    logger.debug("Old result ( {} ) is a failure, skipping merge of it's single statistic ( {} ).",
                            oldResult, oldSingleStatistic);
                    continue;
                } else {
                    throw new IllegalStateException("Could not find old result's ( " + oldResult
                            + " ) single statistic's ( " + oldSingleStatistic + " ) CSV file.");
                }
            }
            oldSingleStatistic.unhibernatePointList();
            singleStatistic.setPointList(oldSingleStatistic.getPointList());
            oldSingleStatistic.hibernatePointList();
        }
        // Skip oldResult.reportDirectory
        // Skip oldResult.usedMemoryAfterInputSolution
        newResult.succeeded = oldResult.succeeded;
        newResult.score = oldResult.score;
        newResult.uninitializedVariableCount = oldResult.uninitializedVariableCount;
        newResult.timeMillisSpent = oldResult.timeMillisSpent;
        newResult.calculateCount = oldResult.calculateCount;

        solverBenchmarkResult.getSingleBenchmarkResultList().add(newResult);
        problemBenchmarkResult.getSingleBenchmarkResultList().add(newResult);
        return newResult;
    }

    @Override
    public String toString() {
        return getName();
    }

}
