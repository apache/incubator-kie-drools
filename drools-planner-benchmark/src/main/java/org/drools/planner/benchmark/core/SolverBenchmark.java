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

package org.drools.planner.benchmark.core;

import java.util.ArrayList;
import java.util.List;

import org.drools.planner.config.solver.SolverConfig;
import org.drools.planner.core.Solver;
import org.drools.planner.core.score.Score;

/**
 * Represents 1 {@link Solver} configuration benchmarked on multiple problem instances (data sets).
 */
public class SolverBenchmark {

    private final DefaultPlannerBenchmark plannerBenchmark;

    private String name = null;

    private SolverConfig solverConfig = null;

    private List<ProblemBenchmark> problemBenchmarkList = null;
    private List<SingleBenchmark> singleBenchmarkList = null;

    private int failureCount = -1;
    private Score totalScore = null;
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

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    // ************************************************************************
    // Benchmark methods
    // ************************************************************************

    public void benchmarkingStarted() {
        // Note: do not call SingleBenchmark.benchmarkingStarted()
        // because DefaultPlannerBenchmark does that already on the unified list
    }

    public void benchmarkingEnded() {
        determineTotalScore();
    }

    protected void determineTotalScore() {
        failureCount = 0;
        totalScore = null;
        for (SingleBenchmark singleBenchmark : singleBenchmarkList) {
            if (singleBenchmark.isFailure()) {
                failureCount++;
            } else {
                if (totalScore == null) {
                    totalScore = singleBenchmark.getScore();
                } else {
                    totalScore = totalScore.add(singleBenchmark.getScore());
                }
            }
        }
    }

    public boolean hasAnySuccess() {
        return singleBenchmarkList.size() - failureCount > 0;
    }

    public boolean hasAnyFailure() {
        return failureCount > 0;
    }

    public boolean isRankingBest() {
        return ranking != null && ranking.intValue() == 0;
    }

    public Score getAverageScore() {
        if (totalScore == null) {
            return null;
        }
        return getTotalScore().divide(singleBenchmarkList.size() - failureCount);
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

}
