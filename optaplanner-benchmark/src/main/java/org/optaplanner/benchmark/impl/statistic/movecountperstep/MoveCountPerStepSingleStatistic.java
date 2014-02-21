/*
 * Copyright 2011 JBoss Inc
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

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.DefaultSolver;

public class MoveCountPerStepSingleStatistic extends SingleStatistic<MoveCountPerStepStatisticPoint> {

    private MoveCountPerStepSingleStatisticListener listener;

    private List<MoveCountPerStepStatisticPoint> pointList;

    public MoveCountPerStepSingleStatistic(SingleBenchmarkResult singleBenchmarkResult) {
        super(singleBenchmarkResult, ProblemStatisticType.MOVE_COUNT_PER_STEP);
        listener = new MoveCountPerStepSingleStatisticListener();
        pointList = new ArrayList<MoveCountPerStepStatisticPoint>();
    }

    public List<MoveCountPerStepStatisticPoint> getPointList() {
        return pointList;
    }

    @Override
    public void setPointList(List<MoveCountPerStepStatisticPoint> pointList) {
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
    
    private class MoveCountPerStepSingleStatisticListener extends SolverPhaseLifecycleListenerAdapter {

        @Override
        public void stepEnded(AbstractStepScope stepScope) {
            if (stepScope instanceof LocalSearchStepScope) {
                localSearchStepEnded((LocalSearchStepScope) stepScope);
            }
        }        
        
        private void localSearchStepEnded(LocalSearchStepScope stepScope) {
            long timeMillisSpent = stepScope.getPhaseScope().calculateSolverTimeMillisSpent();
            pointList.add(new MoveCountPerStepStatisticPoint(timeMillisSpent,
                    new MoveCountPerStepMeasurement(stepScope.getAcceptedMoveCount(), stepScope.getSelectedMoveCount())
            ));
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
        return new MoveCountPerStepStatisticPoint(Long.valueOf(csvLine.get(0)),
                new MoveCountPerStepMeasurement(Long.valueOf(csvLine.get(1)), Long.valueOf(csvLine.get(2))));
    }

}
