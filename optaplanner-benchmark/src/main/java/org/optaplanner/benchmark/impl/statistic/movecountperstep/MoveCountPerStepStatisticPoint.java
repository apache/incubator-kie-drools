package org.optaplanner.benchmark.impl.statistic.movecountperstep;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;

public class MoveCountPerStepStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    private final long acceptedMoveCount;
    private final long selectedMoveCount;

    public MoveCountPerStepStatisticPoint(long timeMillisSpent, long acceptedMoveCount, long selectedMoveCount) {
        this.timeMillisSpent = timeMillisSpent;
        this.acceptedMoveCount = acceptedMoveCount;
        this.selectedMoveCount = selectedMoveCount;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public long getAcceptedMoveCount() {
        return acceptedMoveCount;
    }

    public long getSelectedMoveCount() {
        return selectedMoveCount;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithLongs(timeMillisSpent, acceptedMoveCount, selectedMoveCount);
    }

}
