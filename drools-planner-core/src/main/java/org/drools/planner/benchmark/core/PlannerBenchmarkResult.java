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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.core.score.Score;

public class PlannerBenchmarkResult {

    private SolverBenchmark solverBenchmark = null;
    private PlanningProblemBenchmark planningProblemBenchmark = null;

    private int planningEntityCount = -1;
    private long problemScale = -1;
    private Score score = null;
    private Score winningScoreDifference = null; // compared to winning result (which might not be the overall winner)
    private long timeMillisSpend = -1L;
    private long calculateCount = -1L;

    public SolverBenchmark getSolverBenchmark() {
        return solverBenchmark;
    }

    public void setSolverBenchmark(SolverBenchmark solverBenchmark) {
        this.solverBenchmark = solverBenchmark;
    }

    public PlanningProblemBenchmark getPlanningProblemBenchmark() {
        return planningProblemBenchmark;
    }

    public void setPlanningProblemBenchmark(PlanningProblemBenchmark planningProblemBenchmark) {
        this.planningProblemBenchmark = planningProblemBenchmark;
    }

    public int getPlanningEntityCount() {
        return planningEntityCount;
    }

    public void setPlanningEntityCount(int planningEntityCount) {
        this.planningEntityCount = planningEntityCount;
    }

    public long getProblemScale() {
        return problemScale;
    }

    public void setProblemScale(long problemScale) {
        this.problemScale = problemScale;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public Score getWinningScoreDifference() {
        return winningScoreDifference;
    }

    public void setWinningScoreDifference(Score winningScoreDifference) {
        this.winningScoreDifference = winningScoreDifference;
    }

    public long getTimeMillisSpend() {
        return timeMillisSpend;
    }

    public void setTimeMillisSpend(long timeMillisSpend) {
        this.timeMillisSpend = timeMillisSpend;
    }

    public long getCalculateCount() {
        return calculateCount;
    }

    public void setCalculateCount(long calculateCount) {
        this.calculateCount = calculateCount;
    }

    // ************************************************************************
    // Benchmark methods
    // ************************************************************************

    public Long getAverageCalculateCountPerSecond() {
        long timeMillisSpend = this.timeMillisSpend;
        if (timeMillisSpend == 0L) {
            // Avoid divide by zero exception on a fast CPU
            timeMillisSpend = 1L;
        }
        return calculateCount * 1000L / timeMillisSpend;
    }

}
