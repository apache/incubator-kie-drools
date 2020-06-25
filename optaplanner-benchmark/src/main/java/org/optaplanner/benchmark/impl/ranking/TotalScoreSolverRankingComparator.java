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

import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

/**
 * This ranking {@link Comparator} orders a {@link SolverBenchmarkResult} by its total {@link Score}.
 * It maximize the overall score, so it minimizes the overall cost if all {@link PlanningSolution}s would be executed.
 * <p>
 * When the inputSolutions differ greatly in size or difficulty, this often results in a big difference in
 * {@link Score} magnitude between each {@link PlanningSolution}. For example: score 10 for dataset A versus 1000 for dataset B.
 * In such cases, dataset B would marginalize dataset A.
 * To avoid that, use {@link TotalRankSolverRankingWeightFactory}.
 */
public class TotalScoreSolverRankingComparator implements Comparator<SolverBenchmarkResult> {

    private final Comparator<SolverBenchmarkResult> worstScoreSolverRankingComparator = new WorstScoreSolverRankingComparator();

    @Override
    public int compare(SolverBenchmarkResult a, SolverBenchmarkResult b) {
        ScoreDefinition aScoreDefinition = a.getScoreDefinition();
        return Comparator
                .comparing(SolverBenchmarkResult::getFailureCount, Comparator.reverseOrder())
                .thenComparing(SolverBenchmarkResult::getTotalScore,
                        new ResilientScoreComparator(aScoreDefinition))
                .thenComparing(worstScoreSolverRankingComparator)
                .compare(a, b);

    }

}
