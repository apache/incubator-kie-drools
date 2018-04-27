/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.score.constraint;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Retrievable from {@link ScoreDirector#getConstraintMatchTotals()}.
 */
public final class ConstraintMatchTotal implements Serializable, Comparable<ConstraintMatchTotal> {

    private final String constraintPackage;
    private final String constraintName;

    private final Set<ConstraintMatch> constraintMatchSet;
    private Score score;

    /**
     * @param constraintPackage never null
     * @param constraintName never null
     * @param zeroScore never null
     */
    public ConstraintMatchTotal(String constraintPackage, String constraintName, Score zeroScore) {
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        constraintMatchSet = new LinkedHashSet<>();
        score = zeroScore;
    }

    /**
     * @return never null
     */
    public String getConstraintPackage() {
        return constraintPackage;
    }

    /**
     * @return never null
     */
    public String getConstraintName() {
        return constraintName;
    }

    /**
     * @return never null
     */
    public Set<ConstraintMatch> getConstraintMatchSet() {
        return constraintMatchSet;
    }

    /**
     * @return {@code >= 0}
     */
    public int getConstraintMatchCount() {
        return getConstraintMatchSet().size();
    }

    /**
     * Sum of the {@link #getConstraintMatchSet()}'s {@link ConstraintMatch#getScore()}.
     * @return never null
     */
    public Score getScore() {
        return score;
    }

    /**
     * @return never null
     * @deprecated in favor of {@link #getScore()}
     */
    @Deprecated
    public Score getScoreTotal() {
        return getScore();
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public ConstraintMatch addConstraintMatch(List<Object> justificationList, Score score) {
        this.score = this.score.add(score);
        ConstraintMatch constraintMatch = new ConstraintMatch(constraintPackage, constraintName,
                justificationList, score);
        boolean added = constraintMatchSet.add(constraintMatch);
        if (!added) {
            throw new IllegalStateException("The constraintMatchTotal (" + this
                    + ") could not add constraintMatch (" + constraintMatch
                    + ") to its constraintMatchSet (" + constraintMatchSet + ").");
        }
        return constraintMatch;
    }

    public void removeConstraintMatch(ConstraintMatch constraintMatch) {
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

    public String getConstraintId() {
        return constraintPackage + "/" + constraintName;
    }

    @Override
    public int compareTo(ConstraintMatchTotal other) {
        if (!constraintPackage.equals(other.constraintPackage)) {
            return constraintPackage.compareTo(other.constraintPackage);
        } else if (!constraintName.equals(other.constraintName)) {
            return constraintName.compareTo(other.constraintName);
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ConstraintMatchTotal) {
            ConstraintMatchTotal other = (ConstraintMatchTotal) o;
            return constraintPackage.equals(other.constraintPackage)
                    && constraintName.equals(other.constraintName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ((17 * 37)
                + constraintPackage.hashCode()) * 37
                + constraintName.hashCode();
    }

    @Override
    public String toString() {
        return getConstraintId() + "=" + getScore();
    }

}
