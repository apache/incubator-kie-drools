package org.optaplanner.benchmark.impl.statistic.movecountperstep;

public class MoveCountPerStepMeasurement {

    private final long acceptedMoveCount;
    private final long selectedMoveCount;

    public MoveCountPerStepMeasurement(long acceptedMoveCount, long selectedMoveCount) {
        this.acceptedMoveCount = acceptedMoveCount;
        this.selectedMoveCount = selectedMoveCount;
    }

    public long getAcceptedMoveCount() {
        return acceptedMoveCount;
    }

    public long getSelectedMoveCount() {
        return selectedMoveCount;
    }

}
