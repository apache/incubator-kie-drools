/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.benchmark.impl.statistic.stepscore;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.DefaultSolver;

public class StepScoreSingleStatistic extends SingleStatistic<StepScoreStatisticPoint> {

    private final StepScoreSingleStatisticListener listener;

    private List<StepScoreStatisticPoint> pointList;

    public StepScoreSingleStatistic(SingleBenchmarkResult singleBenchmarkResult) {
        super(singleBenchmarkResult, ProblemStatisticType.STEP_SCORE);
        listener = new StepScoreSingleStatisticListener();
        pointList = new ArrayList<StepScoreStatisticPoint>();
    }

    public List<StepScoreStatisticPoint> getPointList() {
        return pointList;
    }

    @Override
    public void setPointList(List<StepScoreStatisticPoint> pointList) {
        this.pointList = pointList;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void open(Solver solver) {
        ((DefaultSolver) solver).addSolverPhaseLifecycleListener(listener);
    }

    public void close(Solver solver) {
        ((DefaultSolver) solver).removeSolverPhaseLifecycleListener(listener);
    }

    private class StepScoreSingleStatisticListener extends SolverPhaseLifecycleListenerAdapter {

        @Override
        public void stepEnded(AbstractStepScope stepScope) {
            if (stepScope.hasNoUninitializedVariables()) {
                long timeMillisSpent = stepScope.getPhaseScope().calculateSolverTimeMillisSpent();
                pointList.add(new StepScoreStatisticPoint(timeMillisSpent, stepScope.getScore()));
            }
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return StepScoreStatisticPoint.buildCsvLine("timeMillisSpent", "score");
    }

    @Override
    protected StepScoreStatisticPoint createPointFromCsvLine(ScoreDefinition scoreDefinition,
            List<String> csvLine) {
        return new StepScoreStatisticPoint(Long.valueOf(csvLine.get(0)),
                scoreDefinition.parseScore(csvLine.get(1)));
    }

}
