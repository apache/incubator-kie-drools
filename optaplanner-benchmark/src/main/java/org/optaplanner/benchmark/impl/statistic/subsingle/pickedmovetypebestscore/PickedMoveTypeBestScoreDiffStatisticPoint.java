package org.optaplanner.benchmark.impl.statistic.subsingle.pickedmovetypebestscore;

import org.optaplanner.benchmark.impl.aggregator.BenchmarkAggregator;
import org.optaplanner.benchmark.impl.statistic.StatisticPoint;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.CompositeMove;
import org.optaplanner.core.impl.heuristic.move.Move;

public class PickedMoveTypeBestScoreDiffStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    /**
     * Not a {@link Class}{@code <}{@link Move}{@code >} because {@link CompositeMove}s need to be atomized
     * and because that {@link Class} might no longer exist when {@link BenchmarkAggregator} aggregates.
     */
    private final String moveType;
    private final Score bestScoreDiff;

    public PickedMoveTypeBestScoreDiffStatisticPoint(long timeMillisSpent, String moveType, Score bestScoreDiff) {
        this.timeMillisSpent = timeMillisSpent;
        this.moveType = moveType;
        this.bestScoreDiff = bestScoreDiff;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public String getMoveType() {
        return moveType;
    }

    public Score getBestScoreDiff() {
        return bestScoreDiff;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithStrings(timeMillisSpent, moveType, bestScoreDiff.toString());
    }

}
