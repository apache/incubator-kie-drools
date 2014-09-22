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

package org.optaplanner.benchmark.impl.ranking;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link SolverRankingWeightFactory} orders a {@link SolverBenchmarkResult} by how how many time each of it's
 * {@link SingleBenchmarkResult} beat the {@link SingleBenchmarkResult} of the other {@link SolverBenchmarkResult}s.
 * It maximizes the overall ranking.
 * <p/>
 * When the inputSolutions differ greatly in size or difficulty, this often produces a difference in
 * {@link Score} magnitude between each {@link Solution}. For example: score 10 for dataset A versus 1000 for dataset B.
 * In such cases, this ranking is more fair than {@link TotalScoreSolverRankingComparator},
 * because in this ranking, dataset B wouldn't marginalize dataset A.
 */
public class TotalRankSolverRankingWeightFactory implements SolverRankingWeightFactory {

    public Comparable createRankingWeight(List<SolverBenchmarkResult> solverBenchmarkResultList, SolverBenchmarkResult solverBenchmarkResult) {
        int betterCount = 0;
        int equalCount = 0;
        List<Score> scoreList = solverBenchmarkResult.getScoreList();
        for (SolverBenchmarkResult otherSolverBenchmarkResult : solverBenchmarkResultList) {
            if (otherSolverBenchmarkResult != solverBenchmarkResult) {
                List<Score> otherScoreList = otherSolverBenchmarkResult.getScoreList();
                // TODO the scoreList.size() can differ between SolverBenchmarks
                for (int i = 0; i < scoreList.size(); i++) {
                    Score score = scoreList.get(i);
                    Score otherScore = otherScoreList.get(i);
                    int scoreComparison = score.compareTo(otherScore);
                    if (scoreComparison > 0) {
                        betterCount++;
                    } else if (scoreComparison == 0) {
                        equalCount++;
                    }
                }
            }
        }
        return new TotalRankSolverRankingWeight(solverBenchmarkResult, betterCount, equalCount);
    }

    public static class TotalRankSolverRankingWeight
            implements Comparable<TotalRankSolverRankingWeight> {

        private final Comparator<SolverBenchmarkResult> totalScoreSolverRankingComparator
                = new TotalScoreSolverRankingComparator();

        private SolverBenchmarkResult solverBenchmarkResult;
        private int betterCount;
        private int equalCount;

        public TotalRankSolverRankingWeight(SolverBenchmarkResult solverBenchmarkResult,
                int betterCount, int equalCount) {
            this.solverBenchmarkResult = solverBenchmarkResult;
            this.betterCount = betterCount;
            this.equalCount = equalCount;
        }

        public int compareTo(TotalRankSolverRankingWeight other) {
            return new CompareToBuilder()
                    .append(betterCount, other.betterCount)
                    .append(equalCount, other.equalCount)
                    .append(solverBenchmarkResult, other.solverBenchmarkResult, totalScoreSolverRankingComparator)
                    .toComparison();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o instanceof TotalRankSolverRankingWeight) {
                TotalRankSolverRankingWeight other = (TotalRankSolverRankingWeight) o;
                return new EqualsBuilder()
                        .append(betterCount, other.betterCount)
                        .append(equalCount, other.equalCount)
                        .appendSuper(totalScoreSolverRankingComparator
                                .compare(solverBenchmarkResult, other.solverBenchmarkResult) == 0)
                        .isEquals();
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(betterCount)
                    .append(equalCount)
                    .toHashCode();
        }

    }

}
