package org.optaplanner.benchmark.impl.ranking;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link SolverRankingWeightFactory} orders a {@link SolverBenchmarkResult} by how many times each of its
 * {@link SingleBenchmarkResult}s beat {@link SingleBenchmarkResult}s of the other {@link SolverBenchmarkResult}.
 * It maximizes the overall ranking.
 * <p>
 * When the inputSolutions differ greatly in size or difficulty, this often produces a difference in
 * {@link Score} magnitude between each {@link PlanningSolution}. For example: score 10 for dataset A versus 1000 for dataset B.
 * In such cases, this ranking is more fair than {@link TotalScoreSolverRankingComparator},
 * because in this ranking, dataset B wouldn't marginalize dataset A.
 */
public class TotalRankSolverRankingWeightFactory implements SolverRankingWeightFactory {

    private final Comparator<SingleBenchmarkResult> singleBenchmarkRankingComparator =
            new TotalScoreSingleBenchmarkRankingComparator();

    @Override
    public Comparable createRankingWeight(List<SolverBenchmarkResult> solverBenchmarkResultList,
            SolverBenchmarkResult solverBenchmarkResult) {
        int betterCount = 0;
        int equalCount = 0;
        int lowerCount = 0;
        List<SingleBenchmarkResult> singleBenchmarkResultList = solverBenchmarkResult.getSingleBenchmarkResultList();
        for (SingleBenchmarkResult single : singleBenchmarkResultList) {
            List<SingleBenchmarkResult> otherSingleList = single.getProblemBenchmarkResult().getSingleBenchmarkResultList();
            for (SingleBenchmarkResult otherSingle : otherSingleList) {
                if (single == otherSingle) {
                    continue;
                }
                int scoreComparison = singleBenchmarkRankingComparator.compare(single, otherSingle);
                if (scoreComparison > 0) {
                    betterCount++;
                } else if (scoreComparison == 0) {
                    equalCount++;
                } else {
                    lowerCount++;
                }
            }
        }
        return new TotalRankSolverRankingWeight(solverBenchmarkResult, betterCount, equalCount, lowerCount);
    }

    public static class TotalRankSolverRankingWeight implements Comparable<TotalRankSolverRankingWeight> {

        private final Comparator<SolverBenchmarkResult> totalScoreSolverRankingComparator =
                new TotalScoreSolverRankingComparator();

        private SolverBenchmarkResult solverBenchmarkResult;
        private int betterCount;
        private int equalCount;
        private int lowerCount;

        public SolverBenchmarkResult getSolverBenchmarkResult() {
            return solverBenchmarkResult;
        }

        public int getBetterCount() {
            return betterCount;
        }

        public int getEqualCount() {
            return equalCount;
        }

        public int getLowerCount() {
            return lowerCount;
        }

        public TotalRankSolverRankingWeight(SolverBenchmarkResult solverBenchmarkResult,
                int betterCount, int equalCount, int lowerCount) {
            this.solverBenchmarkResult = solverBenchmarkResult;
            this.betterCount = betterCount;
            this.equalCount = equalCount;
            this.lowerCount = lowerCount;
        }

        @Override
        public int compareTo(TotalRankSolverRankingWeight other) {
            return Comparator
                    .comparingInt(TotalRankSolverRankingWeight::getBetterCount)
                    .thenComparingInt(TotalRankSolverRankingWeight::getEqualCount)
                    .thenComparingInt(TotalRankSolverRankingWeight::getLowerCount)
                    .thenComparing(TotalRankSolverRankingWeight::getSolverBenchmarkResult, totalScoreSolverRankingComparator) // Tie-breaker
                    .compare(this, other);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o instanceof TotalRankSolverRankingWeight) {
                TotalRankSolverRankingWeight other = (TotalRankSolverRankingWeight) o;
                return this.compareTo(other) == 0;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(betterCount, equalCount, lowerCount);
        }

    }

}
