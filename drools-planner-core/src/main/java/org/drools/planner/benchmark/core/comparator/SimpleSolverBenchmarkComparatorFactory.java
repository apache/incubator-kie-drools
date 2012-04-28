/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.benchmark.core.comparator;

import java.util.Comparator;
import java.util.List;

import org.drools.planner.benchmark.api.SolverBenchmarkComparatorFactory;
import org.drools.planner.benchmark.core.SolverBenchmark;

/**
 * This benchmark ranker simply ranks solver benchmarks by their total score.
 */
public class SimpleSolverBenchmarkComparatorFactory implements SolverBenchmarkComparatorFactory {

    /**
     * Rank the benchmarks based on their total score.
     */
    public Comparator<SolverBenchmark> createSolverBenchmarkComparator(List<SolverBenchmark> solverBenchmarkList) {
        return new TotalScoreSolverBenchmarkComparator();
    }

}
