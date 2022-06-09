package org.optaplanner.benchmark.impl.statistic.subsingle.pickedmovetypestepscore;

import org.optaplanner.benchmark.impl.aggregator.BenchmarkAggregator;
import org.optaplanner.benchmark.impl.statistic.StatisticPoint;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.CompositeMove;
import org.optaplanner.core.impl.heuristic.move.Move;

public class PickedMoveTypeStepScoreDiffStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    /**
     * Not a {@link Class}{@code <}{@link Move}{@code >} because {@link CompositeMove}s need to be atomized
     * and because that {@link Class} might no longer exist when {@link BenchmarkAggregator} aggregates.
     */
    private final String moveType;
    private final Score stepScoreDiff;

    public PickedMoveTypeStepScoreDiffStatisticPoint(long timeMillisSpent, String moveType, Score stepScoreDiff) {
        this.timeMillisSpent = timeMillisSpent;
        this.moveType = moveType;
        this.stepScoreDiff = stepScoreDiff;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public String getMoveType() {
        return moveType;
    }

    public Score getStepScoreDiff() {
        return stepScoreDiff;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithStrings(timeMillisSpent, moveType, stepScoreDiff.toString());
    }

}
