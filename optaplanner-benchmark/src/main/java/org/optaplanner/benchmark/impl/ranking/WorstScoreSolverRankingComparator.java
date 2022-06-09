package org.optaplanner.benchmark.impl.ranking;

import java.util.Comparator;
import java.util.List;

import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.api.score.Score;

/**
 * This ranking {@link Comparator} orders a {@link SolverBenchmarkResult} by its worst {@link Score}.
 * It minimizes the worst case scenario.
 */
public class WorstScoreSolverRankingComparator implements Comparator<SolverBenchmarkResult> {

    private final Comparator<SingleBenchmarkResult> singleBenchmarkComparator =
            new TotalScoreSingleBenchmarkRankingComparator();

    @Override
    public int compare(SolverBenchmarkResult a, SolverBenchmarkResult b) {
        List<SingleBenchmarkResult> aSingleBenchmarkResultList = a.getSingleBenchmarkResultList();
        List<SingleBenchmarkResult> bSingleBenchmarkResultList = b.getSingleBenchmarkResultList();
        // Order scores from worst to best
        aSingleBenchmarkResultList.sort(singleBenchmarkComparator);
        bSingleBenchmarkResultList.sort(singleBenchmarkComparator);
        int aSize = aSingleBenchmarkResultList.size();
        int bSize = bSingleBenchmarkResultList.size();
        for (int i = 0; i < aSize && i < bSize; i++) {
            int comparison = singleBenchmarkComparator.compare(aSingleBenchmarkResultList.get(i),
                    bSingleBenchmarkResultList.get(i));
            if (comparison != 0) {
                return comparison;
            }
        }
        return Integer.compare(aSize, bSize);
    }

}
