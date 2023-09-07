/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.benchmark.impl.statistic.scorecalculationspeed;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemBasedSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticPoint;
import org.optaplanner.benchmark.impl.statistic.StatisticRegistry;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

import io.micrometer.core.instrument.Tags;

public class ScoreCalculationSpeedSubSingleStatistic<Solution_>
        extends ProblemBasedSubSingleStatistic<Solution_, ScoreCalculationSpeedStatisticPoint> {

    private long timeMillisThresholdInterval;

    ScoreCalculationSpeedSubSingleStatistic() {
        // For JAXB.
    }

    public ScoreCalculationSpeedSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        this(subSingleBenchmarkResult, 1000L);
    }

    public ScoreCalculationSpeedSubSingleStatistic(SubSingleBenchmarkResult benchmarkResult, long timeMillisThresholdInterval) {
        super(benchmarkResult, ProblemStatisticType.SCORE_CALCULATION_SPEED);
        if (timeMillisThresholdInterval <= 0L) {
            throw new IllegalArgumentException("The timeMillisThresholdInterval (" + timeMillisThresholdInterval
                    + ") must be bigger than 0.");
        }
        this.timeMillisThresholdInterval = timeMillisThresholdInterval;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void open(StatisticRegistry<Solution_> registry, Tags runTag, Solver<Solution_> solver) {
        registry.addListener(SolverMetric.SCORE_CALCULATION_COUNT, new Consumer<Long>() {
            long nextTimeMillisThreshold = timeMillisThresholdInterval;
            long lastTimeMillisSpent = 0;
            final AtomicLong lastScoreCalculationCount = new AtomicLong(0);

            @Override
            public void accept(Long timeMillisSpent) {
                if (timeMillisSpent >= nextTimeMillisThreshold) {
                    registry.getGaugeValue(SolverMetric.SCORE_CALCULATION_COUNT, runTag, scoreCalculationCountNumber -> {
                        long scoreCalculationCount = scoreCalculationCountNumber.longValue();
                        long calculationCountInterval = scoreCalculationCount - lastScoreCalculationCount.get();
                        long timeMillisSpentInterval = timeMillisSpent - lastTimeMillisSpent;
                        if (timeMillisSpentInterval == 0L) {
                            // Avoid divide by zero exception on a fast CPU
                            timeMillisSpentInterval = 1L;
                        }
                        long scoreCalculationSpeed = calculationCountInterval * 1000L / timeMillisSpentInterval;
                        pointList.add(new ScoreCalculationSpeedStatisticPoint(timeMillisSpent, scoreCalculationSpeed));
                        lastScoreCalculationCount.set(scoreCalculationCount);
                    });
                    lastTimeMillisSpent = timeMillisSpent;
                    nextTimeMillisThreshold += timeMillisThresholdInterval;
                    if (nextTimeMillisThreshold < timeMillisSpent) {
                        nextTimeMillisThreshold = timeMillisSpent;
                    }
                }
            }
        });
    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return StatisticPoint.buildCsvLine("timeMillisSpent", "scoreCalculationSpeed");
    }

    @Override
    protected ScoreCalculationSpeedStatisticPoint createPointFromCsvLine(ScoreDefinition<?> scoreDefinition,
            List<String> csvLine) {
        return new ScoreCalculationSpeedStatisticPoint(Long.parseLong(csvLine.get(0)),
                Long.parseLong(csvLine.get(1)));
    }

}
