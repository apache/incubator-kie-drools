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

package org.optaplanner.benchmark.impl.statistic.movecountperstep;

import java.util.List;

import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemBasedSubSingleStatistic;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.DefaultSolver;

public class MoveCountPerStepSubSingleStatistic<Solution_>
        extends ProblemBasedSubSingleStatistic<Solution_, MoveCountPerStepStatisticPoint> {

    private MoveCountPerStepSubSingleStatisticListener listener;

    public MoveCountPerStepSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        super(subSingleBenchmarkResult, ProblemStatisticType.MOVE_COUNT_PER_STEP);
        listener = new MoveCountPerStepSubSingleStatisticListener();
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

    private class MoveCountPerStepSubSingleStatisticListener extends PhaseLifecycleListenerAdapter<Solution_> {

        @Override
        public void stepEnded(AbstractStepScope<Solution_> stepScope) {
            if (stepScope instanceof LocalSearchStepScope) {
                localSearchStepEnded((LocalSearchStepScope<Solution_>) stepScope);
            }
        }

        private void localSearchStepEnded(LocalSearchStepScope<Solution_> stepScope) {
            long timeMillisSpent = stepScope.getPhaseScope().calculateSolverTimeMillisSpentUpToNow();
            pointList.add(new MoveCountPerStepStatisticPoint(timeMillisSpent,
                    new MoveCountPerStepMeasurement(stepScope.getAcceptedMoveCount(), stepScope.getSelectedMoveCount())));
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return MoveCountPerStepStatisticPoint.buildCsvLine("timeMillisSpent", "acceptedMoveCount", "selectedMoveCount");
    }

    @Override
    protected MoveCountPerStepStatisticPoint createPointFromCsvLine(ScoreDefinition scoreDefinition,
            List<String> csvLine) {
        return new MoveCountPerStepStatisticPoint(Long.parseLong(csvLine.get(0)),
                new MoveCountPerStepMeasurement(Long.parseLong(csvLine.get(1)), Long.parseLong(csvLine.get(2))));
    }

}
