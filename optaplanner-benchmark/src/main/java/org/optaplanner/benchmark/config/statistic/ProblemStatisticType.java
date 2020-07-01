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

package org.optaplanner.benchmark.config.statistic;

import javax.xml.bind.annotation.XmlEnum;

import org.apache.commons.lang3.StringUtils;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticType;
import org.optaplanner.benchmark.impl.statistic.bestscore.BestScoreProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.bestsolutionmutation.BestSolutionMutationProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.memoryuse.MemoryUseProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.movecountperstep.MoveCountPerStepProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.scorecalculationspeed.ScoreCalculationSpeedProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.stepscore.StepScoreProblemStatistic;

@XmlEnum
public enum ProblemStatisticType implements StatisticType {
    BEST_SCORE,
    STEP_SCORE,
    SCORE_CALCULATION_SPEED,
    BEST_SOLUTION_MUTATION,
    MOVE_COUNT_PER_STEP,
    MEMORY_USE;

    @Override
    public String getLabel() {
        return StringUtils.capitalize(name().replace('_', ' ').toLowerCase());
    }

    public ProblemStatistic buildProblemStatistic(ProblemBenchmarkResult problemBenchmarkResult) {
        // Keep in sync with ProblemStatistic XStreamInclude list
        switch (this) {
            case BEST_SCORE:
                return new BestScoreProblemStatistic(problemBenchmarkResult);
            case STEP_SCORE:
                return new StepScoreProblemStatistic(problemBenchmarkResult);
            case SCORE_CALCULATION_SPEED:
                return new ScoreCalculationSpeedProblemStatistic(problemBenchmarkResult);
            case BEST_SOLUTION_MUTATION:
                return new BestSolutionMutationProblemStatistic(problemBenchmarkResult);
            case MOVE_COUNT_PER_STEP:
                return new MoveCountPerStepProblemStatistic(problemBenchmarkResult);
            case MEMORY_USE:
                return new MemoryUseProblemStatistic(problemBenchmarkResult);
            default:
                throw new IllegalStateException("The problemStatisticType (" + this + ") is not implemented.");
        }
    }

    public boolean hasScoreLevels() {
        return this == BEST_SCORE
                || this == STEP_SCORE;
    }

}
