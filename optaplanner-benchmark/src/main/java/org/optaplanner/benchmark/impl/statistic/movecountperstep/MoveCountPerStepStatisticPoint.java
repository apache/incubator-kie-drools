package org.optaplanner.benchmark.impl.statistic.movecountperstep;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;

public class MoveCountPerStepStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    private final MoveCountPerStepMeasurement moveCountPerStepMeasurement;

    public MoveCountPerStepStatisticPoint(long timeMillisSpent,
            MoveCountPerStepMeasurement moveCountPerStepMeasurement) {
        this.timeMillisSpent = timeMillisSpent;
        this.moveCountPerStepMeasurement = moveCountPerStepMeasurement;
    }

    public MoveCountPerStepMeasurement getMoveCountPerStepMeasurement() {
        return moveCountPerStepMeasurement;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithLongs(timeMillisSpent, moveCountPerStepMeasurement.getAcceptedMoveCount(),
                moveCountPerStepMeasurement.getSelectedMoveCount());
    }

}
