/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.ranking;

import java.util.List;

import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;

/**
 * Defines an interface for classes that will be used to rank solver benchmarks
 * in order of their respective performance.
 */
public interface SolverRankingWeightFactory {

    /**
     * The ranking function. Takes the provided solverBenchmarkResultList and ranks them.
     *
     * @param solverBenchmarkResultList never null
     * @param solverBenchmarkResult never null
     * @return never null
     */
    Comparable createRankingWeight(List<SolverBenchmarkResult> solverBenchmarkResultList,
            SolverBenchmarkResult solverBenchmarkResult);

}
