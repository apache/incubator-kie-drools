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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.statistic.AbstractSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.statistic.StatisticType;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.DefaultSolver;

public class MoveCountPerStepSingleStatistic extends AbstractSingleStatistic<MoveCountPerStepSingleStatisticPoint> {

    private MoveCountPerStepSingleStatisticListener listener = new MoveCountPerStepSingleStatisticListener();

    private List<MoveCountPerStepSingleStatisticPoint> pointList = new ArrayList<MoveCountPerStepSingleStatisticPoint>();

    public MoveCountPerStepSingleStatistic(SingleBenchmark singleBenchmark) {
        super(singleBenchmark, ProblemStatisticType.MOVE_COUNT_PER_STEP);
    }

    public List<MoveCountPerStepSingleStatisticPoint> getPointList() {
        return pointList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void open(Solver solver) {
        ((DefaultSolver) solver).addSolverPhaseLifecycleListener(listener);
    }

    public void close(Solver solver) {
        ((DefaultSolver) solver).removeSolverPhaseLifecycleListener(listener);
        writeCsvStatisticFile();
    }
    
    private class MoveCountPerStepSingleStatisticListener extends SolverPhaseLifecycleListenerAdapter {

        @Override
        public void stepEnded(AbstractStepScope stepScope) {
            if (stepScope instanceof LocalSearchStepScope) {
                localSearchStepEnded((LocalSearchStepScope) stepScope);
            }
        }        
        
        private void localSearchStepEnded(LocalSearchStepScope stepScope) {
            long timeMillisSpend = stepScope.getPhaseScope().calculateSolverTimeMillisSpend();
            pointList.add(new MoveCountPerStepSingleStatisticPoint(timeMillisSpend,
                    new MoveCountPerStepMeasurement(stepScope.getAcceptedMoveCount(), stepScope.getSelectedMoveCount())
            ));
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected List<String> getCsvHeader() {
        return MoveCountPerStepSingleStatisticPoint.buildCsvLine("timeMillisSpend", "acceptedMoveCount", "selectedMoveCount");
    }

    @Override
    protected MoveCountPerStepSingleStatisticPoint createPointFromCsvLine(ScoreDefinition scoreDefinition,
            List<String> csvLine) {
        return new MoveCountPerStepSingleStatisticPoint(Long.valueOf(csvLine.get(0)),
                new MoveCountPerStepMeasurement(Long.valueOf(csvLine.get(1)), Long.valueOf(csvLine.get(2))));
    }

}
