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

package org.optaplanner.benchmark.impl.statistic.calculatecount;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.statistic.AbstractSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class CalculateCountSingleStatistic extends AbstractSingleStatistic<CalculateCountSingleStatisticPoint> {

    private long timeMillisThresholdInterval;
    private long nextTimeMillisThreshold;

    private final CalculateCountSingleStatisticListener listener = new CalculateCountSingleStatisticListener();

    private long lastTimeMillisSpend = 0L;
    private long lastCalculateCount = 0L;

    private List<CalculateCountSingleStatisticPoint> pointList = new ArrayList<CalculateCountSingleStatisticPoint>();

    public CalculateCountSingleStatistic(SingleBenchmark singleBenchmark) {
        this(singleBenchmark, 1000L);
    }

    public CalculateCountSingleStatistic(SingleBenchmark singleBenchmark, long timeMillisThresholdInterval) {
        super(singleBenchmark, ProblemStatisticType.CALCULATE_COUNT_PER_SECOND);
        if (timeMillisThresholdInterval <= 0L) {
            throw new IllegalArgumentException("The timeMillisThresholdInterval (" + timeMillisThresholdInterval
                    + ") must be bigger than 0.");
        }
        this.timeMillisThresholdInterval = timeMillisThresholdInterval;
        nextTimeMillisThreshold = timeMillisThresholdInterval;
    }

    public List<CalculateCountSingleStatisticPoint> getPointList() {
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
    }

    private class CalculateCountSingleStatisticListener extends SolverPhaseLifecycleListenerAdapter {

        @Override
        public void stepEnded(AbstractStepScope stepScope) {
            long timeMillisSpend = stepScope.getPhaseScope().calculateSolverTimeMillisSpend();
            if (timeMillisSpend >= nextTimeMillisThreshold) {
                DefaultSolverScope solverScope = stepScope.getPhaseScope().getSolverScope();
                long calculateCount = solverScope.getCalculateCount();
                long calculateCountInterval = calculateCount - lastCalculateCount;
                long timeMillisSpendInterval = timeMillisSpend - lastTimeMillisSpend;
                if (timeMillisSpendInterval == 0L) {
                    // Avoid divide by zero exception on a fast CPU
                    timeMillisSpendInterval = 1L;
                }
                long averageCalculateCountPerSecond = calculateCountInterval * 1000L / timeMillisSpendInterval;
                pointList.add(new CalculateCountSingleStatisticPoint(timeMillisSpend, averageCalculateCountPerSecond));
                lastCalculateCount = calculateCount;

                lastTimeMillisSpend = timeMillisSpend;
                nextTimeMillisThreshold += timeMillisThresholdInterval;
                if (nextTimeMillisThreshold < timeMillisSpend) {
                    nextTimeMillisThreshold = timeMillisSpend;
                }
            }
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected List<String> getCsvHeader() {
        return CalculateCountSingleStatisticPoint.buildCsvLine("timeMillisSpend", "calculateCountPerSecond");
    }

    @Override
    protected CalculateCountSingleStatisticPoint createPointFromCsvLine(ScoreDefinition scoreDefinition,
            List<String> csvLine) {
        return new CalculateCountSingleStatisticPoint(Long.valueOf(csvLine.get(0)),
                Long.valueOf(csvLine.get(1)));
    }

}
