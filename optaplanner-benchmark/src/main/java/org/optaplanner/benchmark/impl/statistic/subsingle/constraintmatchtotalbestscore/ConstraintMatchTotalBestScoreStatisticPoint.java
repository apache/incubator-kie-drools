package org.optaplanner.benchmark.impl.statistic.subsingle.constraintmatchtotalbestscore;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;
import org.optaplanner.core.api.score.Score;

public class ConstraintMatchTotalBestScoreStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    private final String constraintPackage;
    private final String constraintName;
    private final int constraintMatchCount;
    private final Score scoreTotal;

    public ConstraintMatchTotalBestScoreStatisticPoint(long timeMillisSpent,
            String constraintPackage, String constraintName,
            int constraintMatchCount, Score scoreTotal) {
        this.timeMillisSpent = timeMillisSpent;
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.constraintMatchCount = constraintMatchCount;
        this.scoreTotal = scoreTotal;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public String getConstraintPackage() {
        return constraintPackage;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public int getConstraintMatchCount() {
        return constraintMatchCount;
    }

    public Score getScoreTotal() {
        return scoreTotal;
    }

    public String getConstraintId() {
        return constraintPackage + "/" + constraintName;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithStrings(timeMillisSpent, constraintPackage, constraintName,
                Integer.toString(constraintMatchCount), scoreTotal.toString());
    }

}
