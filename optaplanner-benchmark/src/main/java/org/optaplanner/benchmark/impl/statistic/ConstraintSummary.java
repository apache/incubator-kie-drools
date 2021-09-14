package org.optaplanner.benchmark.impl.statistic;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;

public class ConstraintSummary<Score_ extends Score<Score_>> {

    private final String constraintPackage;
    private final String constraintName;
    private final Score_ score;
    private final int count;

    public ConstraintSummary(String constraintPackage, String constraintName, Score_ score, int count) {
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.score = score;
        this.count = count;
    }

    public String getConstraintPackage() {
        return constraintPackage;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public Score_ getScore() {
        return score;
    }

    public int getCount() {
        return count;
    }

    public String getConstraintId() {
        return ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName);
    }
}
