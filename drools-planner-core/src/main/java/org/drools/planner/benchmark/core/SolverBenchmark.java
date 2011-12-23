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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.drools.planner.config.solver.SolverConfig;
import org.drools.planner.core.score.Score;

public class SolverBenchmark {

    private String name = null;

    private SolverConfig solverConfig = null;
    private List<File> unsolvedSolutionFileList = null;

    private List<PlannerBenchmarkResult> plannerBenchmarkResultList = null;

    // Ranking starts from 0
    private Integer ranking = null;

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

    public List<File> getUnsolvedSolutionFileList() {
        return unsolvedSolutionFileList;
    }

    public void setUnsolvedSolutionFileList(List<File> unsolvedSolutionFileList) {
        this.unsolvedSolutionFileList = unsolvedSolutionFileList;
    }

    public List<PlannerBenchmarkResult> getPlannerBenchmarkResultList() {
        return plannerBenchmarkResultList;
    }

    public void setPlannerBenchmarkResultList(List<PlannerBenchmarkResult> plannerBenchmarkResultList) {
        this.plannerBenchmarkResultList = plannerBenchmarkResultList;
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
        resetPlannerBenchmarkResultList();
    }

    public void resetPlannerBenchmarkResultList() {
        plannerBenchmarkResultList = new ArrayList<PlannerBenchmarkResult>();
        for (File unsolvedSolutionFile : unsolvedSolutionFileList) {
            PlannerBenchmarkResult result = new PlannerBenchmarkResult();
            result.setUnsolvedSolutionFile(unsolvedSolutionFile);
            plannerBenchmarkResultList.add(result);
        }
    }

    public List<Score> getScoreList() {
        List<Score> scoreList = new ArrayList<Score>(plannerBenchmarkResultList.size());
        for (PlannerBenchmarkResult plannerBenchmarkResult : plannerBenchmarkResultList) {
            scoreList.add(plannerBenchmarkResult.getScore());
        }
        return scoreList;
    }

    /**
     * @return the total score
     */
    public Score getTotalScore() {
        Score totalScore = null;
        for (PlannerBenchmarkResult plannerBenchmarkResult : plannerBenchmarkResultList) {
            if (totalScore == null) {
                totalScore = plannerBenchmarkResult.getScore();
            } else {
                totalScore = totalScore.add(plannerBenchmarkResult.getScore());
            }
        }
        return totalScore;
    }

    /**
     * @return the average score
     */
    public Score getAverageScore() {
        return getTotalScore().divide(plannerBenchmarkResultList.size());
    }

    public void benchmarkingEnded() {

    }

}
