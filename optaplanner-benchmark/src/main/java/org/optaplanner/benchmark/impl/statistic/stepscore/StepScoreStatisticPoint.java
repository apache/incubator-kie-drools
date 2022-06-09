package org.optaplanner.benchmark.impl.statistic.stepscore;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;
import org.optaplanner.core.api.score.Score;

public class StepScoreStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    private final Score score;

    public StepScoreStatisticPoint(long timeMillisSpent, Score score) {
        this.timeMillisSpent = timeMillisSpent;
        this.score = score;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public Score getScore() {
        return score;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithStrings(timeMillisSpent, score.toString());
    }

}
