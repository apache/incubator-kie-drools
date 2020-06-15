/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemBasedSubSingleStatistic;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.DefaultSolver;

public class StepScoreSubSingleStatistic<Solution_>
        extends ProblemBasedSubSingleStatistic<Solution_, StepScoreStatisticPoint> {

    private final StepScoreSubSingleStatisticListener listener;

    public StepScoreSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        super(subSingleBenchmarkResult, ProblemStatisticType.STEP_SCORE);
        listener = new StepScoreSubSingleStatisticListener();
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void open(Solver<Solution_> solver) {
        ((DefaultSolver<Solution_>) solver).addPhaseLifecycleListener(listener);
    }

    @Override
    public void close(Solver<Solution_> solver) {
        ((DefaultSolver<Solution_>) solver).removePhaseLifecycleListener(listener);
    }

    private class StepScoreSubSingleStatisticListener extends PhaseLifecycleListenerAdapter<Solution_> {

        @Override
        public void stepEnded(AbstractStepScope<Solution_> stepScope) {
            if (stepScope.getScore().isSolutionInitialized()) {
                long timeMillisSpent = stepScope.getPhaseScope().calculateSolverTimeMillisSpentUpToNow();
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
        return new StepScoreStatisticPoint(Long.parseLong(csvLine.get(0)),
                scoreDefinition.parseScore(csvLine.get(1)));
    }

}
