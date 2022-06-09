package org.optaplanner.core.impl.score.constraint;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;

public final class DefaultConstraintMatchTotal<Score_ extends Score<Score_>> implements ConstraintMatchTotal<Score_>,
        Comparable<DefaultConstraintMatchTotal<Score_>> {

    private final String constraintPackage;
    private final String constraintName;
    private final String constraintId;
    private final Score_ constraintWeight;

    private final Set<ConstraintMatch<Score_>> constraintMatchSet = new LinkedHashSet<>();
    private Score_ score;

    public DefaultConstraintMatchTotal(String constraintPackage, String constraintName) {
        this.constraintPackage = requireNonNull(constraintPackage);
        this.constraintName = requireNonNull(constraintName);
        this.constraintId = ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName);
        this.constraintWeight = null;
    }

    public DefaultConstraintMatchTotal(String constraintPackage, String constraintName, Score_ constraintWeight) {
        this.constraintPackage = requireNonNull(constraintPackage);
        this.constraintName = requireNonNull(constraintName);
        this.constraintId = ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName);
        this.constraintWeight = requireNonNull(constraintWeight);
        this.score = constraintWeight.zero();
    }

    @Override
    public String getConstraintPackage() {
        return constraintPackage;
    }

    @Override
    public String getConstraintName() {
        return constraintName;
    }

    @Override
    public Score_ getConstraintWeight() {
        return constraintWeight;
    }

    @Override
    public Set<ConstraintMatch<Score_>> getConstraintMatchSet() {
        return constraintMatchSet;
    }

    @Override
    public Score_ getScore() {
        return score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public ConstraintMatch<Score_> addConstraintMatch(List<Object> justificationList, Score_ score) {
        this.score = this.score == null ? score : this.score.add(score);
        ConstraintMatch<Score_> constraintMatch = new ConstraintMatch<>(constraintPackage, constraintName,
                justificationList, score);
        constraintMatchSet.add(constraintMatch);
        return constraintMatch;
    }

    public void removeConstraintMatch(ConstraintMatch<Score_> constraintMatch) {
        score = score.subtract(constraintMatch.getScore());
        boolean removed = constraintMatchSet.remove(constraintMatch);
        if (!removed) {
            throw new IllegalStateException("The constraintMatchTotal (" + this
                    + ") could not remove constraintMatch (" + constraintMatch
                    + ") from its constraintMatchSet (" + constraintMatchSet + ").");
        }
    }

    // ************************************************************************
    // Infrastructure methods
    // ************************************************************************

    @Override
    public String getConstraintId() {
        return constraintId;
    }

    @Override
    public int compareTo(DefaultConstraintMatchTotal<Score_> other) {
        return constraintId.compareTo(other.constraintId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof DefaultConstraintMatchTotal) {
            DefaultConstraintMatchTotal<Score_> other = (DefaultConstraintMatchTotal<Score_>) o;
            return constraintPackage.equals(other.constraintPackage)
                    && constraintName.equals(other.constraintName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return hash(constraintPackage, constraintName);
    }

    @Override
    public String toString() {
        return getConstraintId() + "=" + score;
    }

}
