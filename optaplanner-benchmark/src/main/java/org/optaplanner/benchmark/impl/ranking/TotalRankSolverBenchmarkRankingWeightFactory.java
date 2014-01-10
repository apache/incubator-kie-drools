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
import org.optaplanner.benchmark.api.ranking.SolverBenchmarkRankingWeightFactory;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.solution.Solution;

/**
 * This {@link SolverBenchmarkRankingWeightFactory} orders a {@link SolverBenchmarkResult} by how how many time each of it's
 * {@link SingleBenchmarkResult} beat the {@link SingleBenchmarkResult} of the other {@link SolverBenchmarkResult}s.
 * It maximizes the overall ranking.
 * <p/>
 * When the inputSolutions differ greatly in size or difficulty, this often produces a difference in
 * {@link Score} magnitude between each {@link Solution}. For example: score 10 for dataset A versus 1000 for dataset B.
 * In such cases, this ranking is more fair than {@link TotalScoreSolverBenchmarkRankingComparator},
 * because in this ranking, dataset B wouldn't marginalize dataset A.
 */
public class TotalRankSolverBenchmarkRankingWeightFactory implements SolverBenchmarkRankingWeightFactory {

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
        return new TotalRankSolverBenchmarkRankingWeight(solverBenchmarkResult, betterCount, equalCount);
    }

    public static class TotalRankSolverBenchmarkRankingWeight
            implements Comparable<TotalRankSolverBenchmarkRankingWeight> {

        private final Comparator<SolverBenchmarkResult> totalScoreSolverBenchmarkRankingComparator
                = new TotalScoreSolverBenchmarkRankingComparator();

        private SolverBenchmarkResult solverBenchmarkResult;
        private int betterCount;
        private int equalCount;

        public TotalRankSolverBenchmarkRankingWeight(SolverBenchmarkResult solverBenchmarkResult,
                int betterCount, int equalCount) {
            this.solverBenchmarkResult = solverBenchmarkResult;
            this.betterCount = betterCount;
            this.equalCount = equalCount;
        }

        public int compareTo(TotalRankSolverBenchmarkRankingWeight other) {
            return new CompareToBuilder()
                    .append(betterCount, other.betterCount)
                    .append(equalCount, other.equalCount)
                    .append(solverBenchmarkResult, other.solverBenchmarkResult, totalScoreSolverBenchmarkRankingComparator)
                    .toComparison();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o instanceof TotalRankSolverBenchmarkRankingWeight) {
                TotalRankSolverBenchmarkRankingWeight other = (TotalRankSolverBenchmarkRankingWeight) o;
                return new EqualsBuilder()
                        .append(betterCount, other.betterCount)
                        .append(equalCount, other.equalCount)
                        .appendSuper(totalScoreSolverBenchmarkRankingComparator
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
