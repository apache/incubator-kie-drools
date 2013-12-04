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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.benchmark.impl.statistic.AbstractSingleStatistic;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.DefaultSolver;

public class StepScoreSingleStatistic extends AbstractSingleStatistic {

    private final StepScoreSingleStatisticListener listener = new StepScoreSingleStatisticListener();

    private List<StepScoreSingleStatisticPoint> pointList = new ArrayList<StepScoreSingleStatisticPoint>();

    public List<StepScoreSingleStatisticPoint> getPointList() {
        return pointList;
    }

    public void setPointList(List<StepScoreSingleStatisticPoint> pointList) {
        this.pointList = pointList;
    }

    public void writeCsvStatistic(File outputFile) {
        SingleStatisticCsv csv = new SingleStatisticCsv();
        for (StepScoreSingleStatisticPoint point : pointList) {
            long timeMillisSpend = point.getTimeMillisSpend();
            Score score = point.getScore();
            if (score != null) {
                csv.addPoint(timeMillisSpend, score.toString());
            }
        }
        csv.writeCsvSingleStatisticFile(outputFile);
    }

    // ************************************************************************
    // Worker methods
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
                long timeMillisSpend = stepScope.getPhaseScope().calculateSolverTimeMillisSpend();
                pointList.add(new StepScoreSingleStatisticPoint(timeMillisSpend, stepScope.getScore()));
            }
        }

    }

}
