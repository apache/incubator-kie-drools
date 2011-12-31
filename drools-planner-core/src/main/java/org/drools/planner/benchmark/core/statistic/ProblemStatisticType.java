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

package org.drools.planner.benchmark.core.statistic;

import org.drools.planner.benchmark.core.statistic.bestscore.BestScoreStatistic;
import org.drools.planner.benchmark.core.statistic.calculatecount.CalculateCountStatistic;
import org.drools.planner.benchmark.core.statistic.memoryuse.MemoryUseStatistic;

public enum ProblemStatisticType {
    BEST_SOLUTION_CHANGED,
    CALCULATE_COUNT_PER_SECOND,
    MEMORY_USE;

    public ProblemStatistic create() {
        switch (this) {
            case BEST_SOLUTION_CHANGED:
                return new BestScoreStatistic();
            case CALCULATE_COUNT_PER_SECOND:
                return new CalculateCountStatistic();
            case MEMORY_USE:
                return new MemoryUseStatistic();
            default:
                throw new IllegalStateException("The problemStatisticType (" + this + ") is not implemented");
        }
    }
}
