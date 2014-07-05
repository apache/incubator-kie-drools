/*
 * Copyright 2014 JBoss Inc
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

import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.PureSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticType;
import org.optaplanner.benchmark.impl.statistic.bestscore.BestScoreProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.bestsolutionmutation.BestSolutionMutationProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.calculatecount.CalculateCountProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.memoryuse.MemoryUseProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.movecountperstep.MoveCountPerStepProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.pickedmovetypebestscore.PickedMoveTypeBestScoreDiffSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.stepscore.StepScoreProblemStatistic;

public enum SingleStatisticType implements StatisticType {
    PICKED_MOVE_TYPE_STEP_SCORE_DIFF,
    PICKED_MOVE_TYPE_BEST_SCORE_DIFF;

    public PureSingleStatistic buildPureSingleStatistic(SingleBenchmarkResult singleBenchmarkResult) {
        // Keep in sync with ProblemStatistic XStreamInclude list
        switch (this) {
//            case PICKED_MOVE_TYPE_STEP_SCORE_DIFF:
//                return new PickedMoveTypeStepScoreDiffSingleStatistic(singleBenchmarkResult);
            case PICKED_MOVE_TYPE_BEST_SCORE_DIFF:
                return new PickedMoveTypeBestScoreDiffSingleStatistic(singleBenchmarkResult);
            default:
                throw new IllegalStateException("The singleStatisticType (" + this + ") is not implemented.");
        }
    }

}
