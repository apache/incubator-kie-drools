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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.lang3.StringEscapeUtils;
import org.optaplanner.benchmark.impl.measurement.ScoreDifferencePercentage;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.report.ReportHelper;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.score.ScoreUtils;
import org.optaplanner.core.impl.solver.XStreamXmlSolverFactory;

/**
 * Represents 1 {@link Solver} configuration benchmarked on multiple problem instances (data sets).
 */
@XStreamAlias("solverBenchmarkResult")
public class SolverBenchmarkResult {

    @XStreamOmitField // Bi-directional relationship restored through BenchmarkResultIO
    private PlannerBenchmarkResult plannerBenchmarkResult;

    private String name = null;

    private Integer subSingleCount = null;

    private SolverConfig solverConfig = null;

    @XStreamImplicit(itemFieldName = "singleBenchmarkResult")
    private List<SingleBenchmarkResult> singleBenchmarkResultList = null;

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    private Integer failureCount = null;
    private Integer uninitializedSolutionCount = null;
    private Integer totalUninitializedVariableCount = null;
    private Integer infeasibleScoreCount = null;
    private Score totalScore = null;
    private Score averageScore = null;
    // Not a Score because
    // - the squaring would cause overflow for relatively small int and long scores.
    // - standard deviation should not be rounded to integer numbers
    private double[] standardDeviationDoubles = null;
    private Score totalWinningScoreDifference = null;
    private ScoreDifferencePercentage averageWorstScoreDifferencePercentage = null;
    // The average of the average is not just the overall average if the SingleBenchmarkResult's timeMillisSpent differ
    private Long averageAverageCalculateCountPerSecond = null;
    private Long averageTimeMillisSpent = null;

    // Ranking starts from 0
    private Integer ranking = null;

    public SolverBenchmarkResult(PlannerBenchmarkResult plannerBenchmarkResult) {
        this.plannerBenchmarkResult = plannerBenchmarkResult;
    }

    public PlannerBenchmarkResult getPlannerBenchmarkResult() {
        return plannerBenchmarkResult;
    }

    public void setPlannerBenchmarkResult(PlannerBenchmarkResult plannerBenchmarkResult) {
        this.plannerBenchmarkResult = plannerBenchmarkResult;
    }

    /**
     * @return never null, filename safe
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSubSingleCount() {
        return subSingleCount;
    }

    public void setSubSingleCount(Integer subSingleCount) {
        this.subSingleCount = subSingleCount;
    }

    public SolverConfig getSolverConfig() {
        return solverConfig;
    }

    public void setSolverConfig(SolverConfig solverConfig) {
        this.solverConfig = solverConfig;
    }

    public List<SingleBenchmarkResult> getSingleBenchmarkResultList() {
        return singleBenchmarkResultList;
    }

    public void setSingleBenchmarkResultList(List<SingleBenchmarkResult> singleBenchmarkResultList) {
        this.singleBenchmarkResultList = singleBenchmarkResultList;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public Integer getUninitializedSolutionCount() {
        return uninitializedSolutionCount;
    }

    public Integer getTotalUninitializedVariableCount() {
        return totalUninitializedVariableCount;
    }

    public Integer getInfeasibleScoreCount() {
        return infeasibleScoreCount;
    }

    public Score getTotalScore() {
        return totalScore;
    }

    public Score getAverageScore() {
        return averageScore;
    }

    public Score getTotalWinningScoreDifference() {
        return totalWinningScoreDifference;
    }

    public ScoreDifferencePercentage getAverageWorstScoreDifferencePercentage() {
        return averageWorstScoreDifferencePercentage;
    }

    public Long getAverageAverageCalculateCountPerSecond() {
        return averageAverageCalculateCountPerSecond;
    }

    public Long getAverageTimeMillisSpent() {
        return averageTimeMillisSpent;
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

    public String getAnchorId() {
        return ReportHelper.escapeHtmlId(name);
    }

    public String getNameWithFavoriteSuffix() {
        if (isFavorite()) {
            return name + " (favorite)";
        }
        return name;
    }

    public int getSuccessCount() {
        return getSingleBenchmarkResultList().size() - getFailureCount();
    }

    public boolean hasAnySuccess() {
        return getSuccessCount() > 0;
    }

    public boolean hasAnyFailure() {
        return failureCount > 0;
    }

    public boolean hasAnyUninitializedSolution() {
        return uninitializedSolutionCount > 0;
    }

    public boolean hasAnyInfeasibleScore() {
        return infeasibleScoreCount > 0;
    }

    public boolean isFavorite() {
        return ranking != null && ranking.intValue() == 0;
    }

    // TODO Do the locale formatting in benchmarkReport.html.ftl - https://issues.jboss.org/browse/PLANNER-169
    public String getStandardDeviationString() {
        if (standardDeviationDoubles == null) {
            return null;
        }
        StringBuilder standardDeviationString = new StringBuilder(standardDeviationDoubles.length * 9);
        boolean first = true;
        for (double standardDeviationDouble : standardDeviationDoubles) {
            if (first) {
                first = false;
            } else {
                standardDeviationString.append("/");
            }
            String abbreviated = Double.toString(standardDeviationDouble);
            // Abbreviate to 2 decimals
            // We don't use DecimalFormat to abbreviate because it's written locale insensitive (like java literals)
            int dotIndex = abbreviated.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex + 3 < abbreviated.length()) {
                abbreviated = abbreviated.substring(0, dotIndex + 3);
            }
            standardDeviationString.append(abbreviated);
        }
        return standardDeviationString.toString();
    }

    public Score getAverageWinningScoreDifference() {
        if (totalWinningScoreDifference == null) {
            return null;
        }
        return totalWinningScoreDifference.divide(getSuccessCount());
    }

    public List<Score> getScoreList() {
        List<Score> scoreList = new ArrayList<Score>(singleBenchmarkResultList.size());
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            scoreList.add(singleBenchmarkResult.getAverageScore());
        }
        return scoreList;
    }

    /**
     * @param problemBenchmarkResult never null
     * @return sometimes null
     */
    public SingleBenchmarkResult findSingleBenchmark(ProblemBenchmarkResult problemBenchmarkResult) {
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            if (problemBenchmarkResult.equals(singleBenchmarkResult.getProblemBenchmarkResult())) {
                return singleBenchmarkResult;
            }
        }
        return null;
    }

    public String getSolverConfigAsHtmlEscapedXml() {
        // TODO reuse a single XStream instance for the entire report
        XStream xStream = XStreamXmlSolverFactory.buildXStream();
        xStream.setMode(XStream.NO_REFERENCES);
        String xml = xStream.toXML(solverConfig);
        return StringEscapeUtils.ESCAPE_HTML4.translate(xml);
    }

    public String getTotalScoreWithUninitializedPrefix() {
        return ScoreUtils.getScoreWithUninitializedPrefix(totalUninitializedVariableCount, totalScore);
    }

    public String getAverageScoreWithUninitializedPrefix() {
        if (getSuccessCount() == 0) {
            return null;
        }
        return ScoreUtils.getScoreWithUninitializedPrefix(
                ConfigUtils.ceilDivide(getTotalUninitializedVariableCount(), getSuccessCount()),
                getAverageScore());
    }

    public EnvironmentMode getEnvironmentMode() {
        return solverConfig.determineEnvironmentMode();
    }

    // ************************************************************************
    // Accumulate methods
    // ************************************************************************

    /**
     * Does not call {@link SingleBenchmarkResult#accumulateResults(BenchmarkReport)},
     * because {@link PlannerBenchmarkResult#accumulateResults(BenchmarkReport)} does that already on
     * {@link PlannerBenchmarkResult#getUnifiedProblemBenchmarkResultList()}.
     * @param benchmarkReport never null
     */
    public void accumulateResults(BenchmarkReport benchmarkReport) {
        determineTotalsAndAverages();
        determineStandardDeviation();
    }

    protected void determineTotalsAndAverages() {
        failureCount = 0;
        boolean firstNonFailure = true;
        totalScore = null;
        totalWinningScoreDifference = null;
        ScoreDifferencePercentage totalWorstScoreDifferencePercentage = null;
        long totalAverageCalculateCountPerSecond = 0L;
        long totalTimeMillisSpent = 0L;
        uninitializedSolutionCount = 0;
        totalUninitializedVariableCount = 0;
        infeasibleScoreCount = 0;
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            if (singleBenchmarkResult.isFailure()) {
                failureCount++;
            } else {
                if (!singleBenchmarkResult.isInitialized()) {
                    uninitializedSolutionCount++;
                    totalUninitializedVariableCount += singleBenchmarkResult.getAverageUninitializedVariableCount();
                } else if (!singleBenchmarkResult.isScoreFeasible()) {
                    infeasibleScoreCount++;
                }
                if (firstNonFailure) {
                    totalScore = singleBenchmarkResult.getAverageScore();
                    totalWinningScoreDifference = singleBenchmarkResult.getWinningScoreDifference();
                    totalWorstScoreDifferencePercentage = singleBenchmarkResult.getWorstScoreDifferencePercentage();
                    totalAverageCalculateCountPerSecond = singleBenchmarkResult.getAverageCalculateCountPerSecond();
                    totalTimeMillisSpent = singleBenchmarkResult.getTimeMillisSpent();
                    firstNonFailure = false;
                } else {
                    totalScore = totalScore.add(singleBenchmarkResult.getAverageScore());
                    totalWinningScoreDifference = totalWinningScoreDifference.add(
                            singleBenchmarkResult.getWinningScoreDifference());
                    totalWorstScoreDifferencePercentage = totalWorstScoreDifferencePercentage.add(
                            singleBenchmarkResult.getWorstScoreDifferencePercentage());
                    totalAverageCalculateCountPerSecond += singleBenchmarkResult.getAverageCalculateCountPerSecond();
                    totalTimeMillisSpent += singleBenchmarkResult.getTimeMillisSpent();
                }
            }
        }
        if (!firstNonFailure) {
            int successCount = getSuccessCount();
            averageScore = totalScore.divide(successCount);
            averageWorstScoreDifferencePercentage = totalWorstScoreDifferencePercentage.divide((double) successCount);
            averageAverageCalculateCountPerSecond = totalAverageCalculateCountPerSecond / (long) successCount;
            averageTimeMillisSpent = totalTimeMillisSpent / (long) successCount;
        }
    }

    protected void determineStandardDeviation() {
        int successCount = getSuccessCount();
        if (successCount <= 0) {
            return;
        }
        // averageScore can no longer be null
        double[] differenceSquaredTotalDoubles = null;
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            if (!singleBenchmarkResult.isFailure()) {
                Score difference = singleBenchmarkResult.getAverageScore().subtract(averageScore);
                // Calculations done on doubles to avoid common overflow when executing with an int score > 500 000
                double[] differenceDoubles = ScoreUtils.extractLevelDoubles(difference);
                if (differenceSquaredTotalDoubles == null) {
                    differenceSquaredTotalDoubles = new double[differenceDoubles.length];
                }
                for (int i = 0; i < differenceDoubles.length; i++) {
                    differenceSquaredTotalDoubles[i] += Math.pow(differenceDoubles[i], 2.0);
                }
            }
        }
        standardDeviationDoubles = new double[differenceSquaredTotalDoubles.length];
        for (int i = 0; i < differenceSquaredTotalDoubles.length; i++) {
            standardDeviationDoubles[i] = Math.pow(differenceSquaredTotalDoubles[i] / successCount, 0.5);
        }
    }

    // ************************************************************************
    // Merger methods
    // ************************************************************************

    protected static Map<SolverBenchmarkResult, SolverBenchmarkResult> createMergeMap(
            PlannerBenchmarkResult newPlannerBenchmarkResult, List<SingleBenchmarkResult> singleBenchmarkResultList) {
        // IdentityHashMap because different SolverBenchmarkResult instances are never merged
        Map<SolverBenchmarkResult, SolverBenchmarkResult> mergeMap
                = new IdentityHashMap<SolverBenchmarkResult, SolverBenchmarkResult>();
        Map<String, Integer> nameCountMap = new HashMap<String, Integer>();
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            SolverBenchmarkResult oldResult = singleBenchmarkResult.getSolverBenchmarkResult();
            if (!mergeMap.containsKey(oldResult)) {
                SolverBenchmarkResult newResult = new SolverBenchmarkResult(newPlannerBenchmarkResult);
                Integer nameCount = nameCountMap.get(oldResult.name);
                if (nameCount == null) {
                    nameCount = 1;
                } else {
                    nameCount++;
                }
                nameCountMap.put(oldResult.name, nameCount);
                newResult.subSingleCount = oldResult.subSingleCount;
                newResult.solverConfig = oldResult.solverConfig;
                newResult.singleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>(
                        oldResult.singleBenchmarkResultList.size());
                mergeMap.put(oldResult, newResult);
                newPlannerBenchmarkResult.getSolverBenchmarkResultList().add(newResult);
            }
        }
        // Make name unique
        for (Map.Entry<SolverBenchmarkResult, SolverBenchmarkResult> entry : mergeMap.entrySet()) {
            SolverBenchmarkResult oldResult = entry.getKey();
            SolverBenchmarkResult newResult = entry.getValue();
            if (nameCountMap.get(oldResult.name) > 1) {
                newResult.name = oldResult.name + " (" + oldResult.getPlannerBenchmarkResult().getName() + ")";
            } else {
                newResult.name = oldResult.name;
            }
        }
        return mergeMap;
    }

    @Override
    public String toString() {
        return getName();
    }

}
