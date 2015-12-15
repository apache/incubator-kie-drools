/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.statistic.bestsolutionmutation;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;

public class BestSolutionMutationStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    private final int mutationCount;

    public BestSolutionMutationStatisticPoint(long timeMillisSpent, int mutationCount) {
        this.timeMillisSpent = timeMillisSpent;
        this.mutationCount = mutationCount;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public int getMutationCount() {
        return mutationCount;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithLongs(timeMillisSpent, mutationCount);
    }

}
