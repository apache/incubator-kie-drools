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

    private List<PlanningProblemBenchmark> planningProblemBenchmarkList = null;
    private List<PlannerBenchmarkResult> plannerBenchmarkResultList = null;

    private Score totalScore = null;
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

    public List<PlanningProblemBenchmark> getPlanningProblemBenchmarkList() {
        return planningProblemBenchmarkList;
    }

    public void setPlanningProblemBenchmarkList(List<PlanningProblemBenchmark> planningProblemBenchmarkList) {
        this.planningProblemBenchmarkList = planningProblemBenchmarkList;
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
        // Note: do not call PlannerBenchmarkResult.benchmarkingStarted()
        // because DefaultPlannerBenchmark does that already on the unified list
    }

    public void benchmarkingEnded() {
        determineTotalScore();
    }

    private void determineTotalScore() {
        totalScore = null;
        for (PlannerBenchmarkResult plannerBenchmarkResult : plannerBenchmarkResultList) {
            if (totalScore == null) {
                totalScore = plannerBenchmarkResult.getScore();
            } else {
                totalScore = totalScore.add(plannerBenchmarkResult.getScore());
            }
        }
    }

    public boolean isRankingBest() {
        return ranking == 0;
    }

    public Score getTotalScore() {
        return totalScore;
    }

    public Score getAverageScore() {
        return getTotalScore().divide(plannerBenchmarkResultList.size());
    }

    public List<Score> getScoreList() {
        List<Score> scoreList = new ArrayList<Score>(plannerBenchmarkResultList.size());
        for (PlannerBenchmarkResult plannerBenchmarkResult : plannerBenchmarkResultList) {
            scoreList.add(plannerBenchmarkResult.getScore());
        }
        return scoreList;
    }

}
