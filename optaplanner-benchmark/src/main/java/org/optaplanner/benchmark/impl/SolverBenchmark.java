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

package org.optaplanner.benchmark.impl;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.optaplanner.benchmark.impl.measurement.ScoreDifferencePercentage;
import org.optaplanner.core.config.solver.XmlSolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.score.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents 1 {@link Solver} configuration benchmarked on multiple problem instances (data sets).
 */
public class SolverBenchmark {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final DefaultPlannerBenchmark plannerBenchmark;

    private String name = null;

    private SolverConfig solverConfig = null;

    private List<ProblemBenchmark> problemBenchmarkList = null;
    private List<SingleBenchmark> singleBenchmarkList = null;

    private int failureCount = -1;
    private Score totalScore = null;
    private Score totalWinningScoreDifference = null;
    private ScoreDifferencePercentage averageWorstScoreDifferencePercentage = null;
    // The average of the average is not just the overall average if the SingleBenchmark's timeMillisSpend differ
    private Long averageAverageCalculateCountPerSecond = null;

    // Ranking starts from 0
    private Integer ranking = null;

    public SolverBenchmark(DefaultPlannerBenchmark plannerBenchmark) {
        this.plannerBenchmark = plannerBenchmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SolverConfig getSolverConfig() {
        return solverConfig;
    }

    public void setSolverConfig(SolverConfig solverConfig) {
        this.solverConfig = solverConfig;
    }

    public List<ProblemBenchmark> getProblemBenchmarkList() {
        return problemBenchmarkList;
    }

    public void setProblemBenchmarkList(List<ProblemBenchmark> problemBenchmarkList) {
        this.problemBenchmarkList = problemBenchmarkList;
    }

    public List<SingleBenchmark> getSingleBenchmarkList() {
        return singleBenchmarkList;
    }

    public void setSingleBenchmarkList(List<SingleBenchmark> singleBenchmarkList) {
        this.singleBenchmarkList = singleBenchmarkList;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public Score getTotalScore() {
        return totalScore;
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

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    // ************************************************************************
    // Benchmark methods
    // ************************************************************************

    public String getNameWithFavoriteSuffix() {
        if (isFavorite()) {
            return name + " (favorite)";
        }
        return name;
    }

    public void benchmarkingStarted() {
        // Note: do not call SingleBenchmark.benchmarkingStarted()
        // because DefaultPlannerBenchmark does that already on the unified list
    }

    public void benchmarkingEnded() {
        determineTotalsAndAverages();
    }

    protected void determineTotalsAndAverages() {
        failureCount = 0;
        boolean firstNonFailure = true;
        totalScore = null;
        totalWinningScoreDifference = null;
        ScoreDifferencePercentage totalWorstScoreDifferencePercentage = null;
        long totalAverageCalculateCountPerSecond = 0L;
        for (SingleBenchmark singleBenchmark : singleBenchmarkList) {
            if (singleBenchmark.isFailure()) {
                failureCount++;
            } else {
                if (firstNonFailure) {
                    totalScore = singleBenchmark.getScore();
                    totalWinningScoreDifference = singleBenchmark.getWinningScoreDifference();
                    totalWorstScoreDifferencePercentage = singleBenchmark.getWorstScoreDifferencePercentage();
                    totalAverageCalculateCountPerSecond = singleBenchmark.getAverageCalculateCountPerSecond();
                    firstNonFailure = false;
                } else {
                    totalScore = totalScore.add(singleBenchmark.getScore());
                    totalWinningScoreDifference = totalWinningScoreDifference.add(
                            singleBenchmark.getWinningScoreDifference());
                    totalWorstScoreDifferencePercentage = totalWorstScoreDifferencePercentage.add(
                            singleBenchmark.getWorstScoreDifferencePercentage());
                    totalAverageCalculateCountPerSecond += singleBenchmark.getAverageCalculateCountPerSecond();
                }
            }
        }
        if (!firstNonFailure) {
            int successCount = getSuccessCount();
            averageWorstScoreDifferencePercentage = totalWorstScoreDifferencePercentage.divide((double) successCount);
            averageAverageCalculateCountPerSecond = totalAverageCalculateCountPerSecond / (long) successCount;
        }
    }

    public int getSuccessCount() {
        return singleBenchmarkList.size() - failureCount;
    }

    public boolean hasAnySuccess() {
        return getSuccessCount() > 0;
    }

    public boolean hasAnyFailure() {
        return failureCount > 0;
    }

    public boolean isFavorite() {
        return ranking != null && ranking.intValue() == 0;
    }

    public Score getAverageScore() {
        if (totalScore == null) {
            return null;
        }
        return totalScore.divide(getSuccessCount());
    }

    public Score getAverageWinningScoreDifference() {
        if (totalWinningScoreDifference == null) {
            return null;
        }
        return totalWinningScoreDifference.divide(getSuccessCount());
    }

    public List<Score> getScoreList() {
        List<Score> scoreList = new ArrayList<Score>(singleBenchmarkList.size());
        for (SingleBenchmark singleBenchmark : singleBenchmarkList) {
            scoreList.add(singleBenchmark.getScore());
        }
        return scoreList;
    }

    /**
     * @param problemBenchmark never null
     * @return sometimes null
     */
    public SingleBenchmark findSingleBenchmark(ProblemBenchmark problemBenchmark) {
        for (SingleBenchmark singleBenchmark : singleBenchmarkList) {
            if (problemBenchmark.equals(singleBenchmark.getProblemBenchmark())) {
                return singleBenchmark;
            }
        }
        return null;
    }

    public String getSolverConfigAsHtmlEscapedXml() {
        // TODO reuse a single XStream instance for the entire report
        XStream xStream = XmlSolverFactory.buildXstream();
        xStream.setMode(XStream.NO_REFERENCES);
        String xml = xStream.toXML(solverConfig);
        return StringEscapeUtils.escapeHtml(xml);
    }

}
