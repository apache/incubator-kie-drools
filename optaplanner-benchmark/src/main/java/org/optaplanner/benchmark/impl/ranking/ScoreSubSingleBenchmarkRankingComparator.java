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

package org.optaplanner.benchmark.impl.ranking;

import java.util.Comparator;

import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public class ScoreSubSingleBenchmarkRankingComparator implements Comparator<SubSingleBenchmarkResult> {

    @Override
    public int compare(SubSingleBenchmarkResult a, SubSingleBenchmarkResult b) {
        ScoreDefinition aScoreDefinition = a.getSingleBenchmarkResult().getSolverBenchmarkResult().getScoreDefinition();
        return Comparator
                // Reverse, less is better (redundant: failed benchmarks don't get ranked at all)
                .comparing(SubSingleBenchmarkResult::hasAnyFailure, Comparator.reverseOrder())
                .thenComparing(SubSingleBenchmarkResult::getScore,
                        new ResilientScoreComparator(aScoreDefinition))
                .compare(a, b);
    }

}
