package org.optaplanner.benchmark.impl.statistic.bestsolutionmutation;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;

public class BestSolutionMutationStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    private final int mutationCount;

    public BestSolutionMutationStatisticPoint(long timeMillisSpent, int mutationCount) {
        this.timeMillisSpent = timeMillisSpent;
        this.mutationCount = mutationCount;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public int getMutationCount() {
        return mutationCount;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithLongs(timeMillisSpent, mutationCount);
    }

}
