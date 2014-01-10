/*
 * Copyright 2010 JBoss Inc
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

import java.util.List;

import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;

/**
 * 1 statistic of {@link ProblemBenchmarkResult}
 */
public interface ProblemStatistic {

    /**
     * @return never null
     */
    ProblemStatisticType getProblemStatisticType();

    /**
     * @return never null
     */
    String getAnchorId();

    /**
     * This method is thread-safe.
     * @param singleBenchmarkResult never null
     * @return never null
     */
    SingleStatistic createSingleStatistic(SingleBenchmarkResult singleBenchmarkResult);

    void accumulateResults(BenchmarkReport benchmarkReport);

    /**
     * @param benchmarkReport never null
     */
    void writeGraphFiles(BenchmarkReport benchmarkReport);

    /**
     * @return never null
     */
    List<String> getWarningList();

}
