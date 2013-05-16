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

package org.optaplanner.benchmark.impl.statistic;

import org.optaplanner.benchmark.impl.ProblemBenchmark;
import org.optaplanner.benchmark.impl.statistic.acceptedselectedmovecount.AcceptedSelectedMoveCountProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.bestscore.BestScoreProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.bestsolutionmutation.BestSolutionMutationProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.calculatecount.CalculateCountProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.improvingsteppercentage.ImprovingStepPercentageProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.memoryuse.MemoryUseProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.stepscore.StepScoreProblemStatistic;

public enum ProblemStatisticType implements StatisticType {
    BEST_SCORE,
    STEP_SCORE,
    CALCULATE_COUNT_PER_SECOND,
    BEST_SOLUTION_MUTATION,
    @Deprecated IMPROVING_STEP_PERCENTAGE,
    MEMORY_USE,
    ACCEPTED_SELECTED_MOVE_COUNT;

    public ProblemStatistic create(ProblemBenchmark problemBenchmark) {
        switch (this) {
            case BEST_SCORE:
                return new BestScoreProblemStatistic(problemBenchmark);
            case STEP_SCORE:
                return new StepScoreProblemStatistic(problemBenchmark);
            case CALCULATE_COUNT_PER_SECOND:
                return new CalculateCountProblemStatistic(problemBenchmark);
            case BEST_SOLUTION_MUTATION:
                return new BestSolutionMutationProblemStatistic(problemBenchmark);
            case IMPROVING_STEP_PERCENTAGE:
                return new ImprovingStepPercentageProblemStatistic(problemBenchmark);
            case MEMORY_USE:
                return new MemoryUseProblemStatistic(problemBenchmark);
            case ACCEPTED_SELECTED_MOVE_COUNT:
                return new AcceptedSelectedMoveCountProblemStatistic(problemBenchmark); 
            default:
                throw new IllegalStateException("The problemStatisticType (" + this + ") is not implemented.");
        }
    }

}
