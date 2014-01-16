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

package org.optaplanner.benchmark.impl.aggregator;

import java.util.List;

import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;

public class BenchmarkAggregator {

    public void aggregate(List<SingleBenchmarkResult> singleBenchmarkResultList) {
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            singleBenchmarkResult.initSingleStatisticMap();
            for (SingleStatistic singleStatistic : singleBenchmarkResult.getSingleStatisticMap().values()) {
                singleStatistic.readCsvStatisticFile();
            }
        }
        PlannerBenchmarkResult mergedResult = PlannerBenchmarkResult.createMergedResult(singleBenchmarkResultList);
    }

}
