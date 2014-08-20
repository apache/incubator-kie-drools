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

package org.optaplanner.benchmark.impl.statistic.single.bestconstraintmatchtotal;

import org.optaplanner.benchmark.impl.aggregator.BenchmarkAggregator;
import org.optaplanner.benchmark.impl.statistic.StatisticPoint;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.CompositeMove;

public class ConstraintMatchTotalBestScoreStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    private final String constraintId;
    private final int constraintMatchCount;
    private final double weightTotal;

    public ConstraintMatchTotalBestScoreStatisticPoint(long timeMillisSpent, String constraintId, int constraintMatchCount, double weightTotal) {
        this.timeMillisSpent = timeMillisSpent;
        this.constraintId = constraintId;
        this.constraintMatchCount = constraintMatchCount;
        this.weightTotal = weightTotal;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public String getConstraintId() {
        return constraintId;
    }

    public int getConstraintMatchCount() {
        return constraintMatchCount;
    }

    public double getWeightTotal() {
        return weightTotal;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithStrings(timeMillisSpent, constraintId,
                Integer.toString(constraintMatchCount), Double.toString(weightTotal));
    }

}
