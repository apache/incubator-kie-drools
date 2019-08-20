/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import java.util.Set;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Explains the {@link Score} of a {@link PlanningSolution}, from the opposite side than {@link ConstraintMatchTotal}.
 * Retrievable from {@link ScoreDirector#getIndictmentMap()}.
 */
public final class Indictment implements Serializable, Comparable<Indictment> {

    private final Object justification;

    private final Set<ConstraintMatch> constraintMatchSet;
    private Score score;

    /**
     * @param justification never null
     * @param zeroScore never null
     */
    public Indictment(Object justification, Score zeroScore) {
        this.justification = justification;
        constraintMatchSet = new LinkedHashSet<>();
        score = zeroScore;
    }

    /**
     * @return never null
     */
    public Object getJustification() {
        return justification;
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

    public void addConstraintMatch(ConstraintMatch constraintMatch) {
        score = score.add(constraintMatch.getScore());
        boolean added = constraintMatchSet.add(constraintMatch);
        if (!added) {
            throw new IllegalStateException("The indictment (" + this
                    + ") could not add constraintMatch (" + constraintMatch
                    + ") to its constraintMatchSet (" + constraintMatchSet + ").");
        }
    }

    public void removeConstraintMatch(ConstraintMatch constraintMatch) {
        score = score.subtract(constraintMatch.getScore());
        boolean removed = constraintMatchSet.remove(constraintMatch);
        if (!removed) {
            throw new IllegalStateException("The indictment (" + this
                    + ") could not remove constraintMatch (" + constraintMatch
                    + ") from its constraintMatchSet (" + constraintMatchSet + ").");
        }
    }

    // ************************************************************************
    // Infrastructure methods
    // ************************************************************************

    @Override
    public int compareTo(Indictment other) {
        if (!(justification instanceof Comparable)) {
            throw new IllegalStateException("The justification (" + justification + ") does not implement "
                    + Comparable.class.getSimpleName() + ", so it cannot be compared with otherJustification ("
                    + other.justification + ").");
        }
        return ((Comparable) justification).compareTo(other.justification);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Indictment) {
            Indictment other = (Indictment) o;
            return justification.equals(other.justification);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return justification.hashCode();
    }

    @Override
    public String toString() {
        return justification + "=" + score;
    }

}
