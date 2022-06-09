package org.optaplanner.benchmark.impl.statistic.scorecalculationspeed;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;

public class ScoreCalculationSpeedStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    private final long scoreCalculationSpeed;

    public ScoreCalculationSpeedStatisticPoint(long timeMillisSpent, long scoreCalculationSpeed) {
        this.timeMillisSpent = timeMillisSpent;
        this.scoreCalculationSpeed = scoreCalculationSpeed;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public long getScoreCalculationSpeed() {
        return scoreCalculationSpeed;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithLongs(timeMillisSpent, scoreCalculationSpeed);
    }

}
