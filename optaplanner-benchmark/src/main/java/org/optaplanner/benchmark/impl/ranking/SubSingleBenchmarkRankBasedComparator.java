package org.optaplanner.benchmark.impl.ranking;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static java.util.Comparator.reverseOrder;

import java.util.Comparator;

import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;

public class SubSingleBenchmarkRankBasedComparator implements Comparator<SubSingleBenchmarkResult> {

    private static final Comparator<SubSingleBenchmarkResult> COMPARATOR =
            // Reverse, less is better (redundant: failed benchmarks don't get ranked at all)
            comparing(SubSingleBenchmarkResult::hasAnyFailure, reverseOrder())
                    .thenComparing(SubSingleBenchmarkResult::getRanking, nullsLast(naturalOrder()));

    @Override
    public int compare(SubSingleBenchmarkResult a, SubSingleBenchmarkResult b) {
        return COMPARATOR.compare(a, b);
    }

}
