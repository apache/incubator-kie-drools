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
import org.optaplanner.benchmark.api.ranking.SolverBenchmarkRankingWeightFactory;
import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.SolverBenchmark;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.solution.Solution;

/**
 * This {@link SolverBenchmarkRankingWeightFactory} orders a {@link SolverBenchmark} by how how many time each of it's
 * {@link SingleBenchmark} beat the {@link SingleBenchmark} of the other {@link SolverBenchmark}s.
 * It maximizes the overall ranking.
 * <p/>
 * When the inputSolutions differ greatly in size or difficulty, this often produces a difference in
 * {@link Score} magnitude between each {@link Solution}. For example: score 10 for dataset A versus 1000 for dataset B.
 * In such cases, this ranking is more fair than {@link TotalScoreSolverBenchmarkRankingComparator},
 * because in this ranking, dataset B wouldn't marginalize dataset A.
 */
public class TotalRankSolverBenchmarkRankingWeightFactory implements SolverBenchmarkRankingWeightFactory {

    public Comparable createRankingWeight(List<SolverBenchmark> solverBenchmarkList, SolverBenchmark solverBenchmark) {
        int betterCount = 0;
        int equalCount = 0;
        List<Score> scoreList = solverBenchmark.getScoreList();
        for (SolverBenchmark otherSolverBenchmark : solverBenchmarkList) {
            if (otherSolverBenchmark != solverBenchmark) {
                List<Score> otherScoreList = otherSolverBenchmark.getScoreList();
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
        return new TotalRankSolverBenchmarkRankingWeight(solverBenchmark, betterCount, equalCount);
    }

    public static class TotalRankSolverBenchmarkRankingWeight
            implements Comparable<TotalRankSolverBenchmarkRankingWeight> {

        private final Comparator<SolverBenchmark> totalScoreSolverBenchmarkRankingComparator
                = new TotalScoreSolverBenchmarkRankingComparator();

        private SolverBenchmark solverBenchmark;
        private int betterCount;
        private int equalCount;

        public TotalRankSolverBenchmarkRankingWeight(SolverBenchmark solverBenchmark,
                int betterCount, int equalCount) {
            this.solverBenchmark = solverBenchmark;
            this.betterCount = betterCount;
            this.equalCount = equalCount;
        }

        public int compareTo(TotalRankSolverBenchmarkRankingWeight other) {
            return new CompareToBuilder()
                    .append(betterCount, other.betterCount)
                    .append(equalCount, other.equalCount)
                    .append(solverBenchmark, other.solverBenchmark, totalScoreSolverBenchmarkRankingComparator)
                    .toComparison();
        }
    }

}
