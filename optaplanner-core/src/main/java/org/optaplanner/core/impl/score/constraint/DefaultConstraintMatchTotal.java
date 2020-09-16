/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.constraint;

import static java.util.Comparator.comparing;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;

public final class DefaultConstraintMatchTotal<Score_ extends Score<Score_>> implements ConstraintMatchTotal<Score_>,
        Comparable<DefaultConstraintMatchTotal<Score_>> {

    private static final Comparator<DefaultConstraintMatchTotal<?>> COMPARATOR =
            comparing((DefaultConstraintMatchTotal<?> constraintMatchTotal) -> constraintMatchTotal.constraintPackage)
                    .thenComparing(DefaultConstraintMatchTotal::getConstraintName);

    private final String constraintPackage;
    private final String constraintName;
    private final Score_ constraintWeight;

    private final Set<ConstraintMatch<Score_>> constraintMatchSet = new LinkedHashSet<>();
    private Score_ score;

    public DefaultConstraintMatchTotal(String constraintPackage, String constraintName, Score_ zeroScore) {
        this(constraintPackage, constraintName, null, zeroScore);
    }

    public DefaultConstraintMatchTotal(String constraintPackage, String constraintName, Score_ constraintWeight,
            Score_ zeroScore) {
        this.constraintPackage = requireNonNull(constraintPackage);
        this.constraintName = requireNonNull(constraintName);
        this.constraintWeight = constraintWeight;
        this.score = requireNonNull(zeroScore);
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
        this.score = this.score.add(score);
        ConstraintMatch<Score_> constraintMatch = new ConstraintMatch<>(constraintPackage, constraintName,
                justificationList, score);
        boolean added = constraintMatchSet.add(constraintMatch);
        if (!added) {
            throw new IllegalStateException("The constraintMatchTotal (" + this
                    + ") could not add constraintMatch (" + constraintMatch
                    + ") to its constraintMatchSet (" + constraintMatchSet + ").");
        }
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
        return ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName);
    }

    @Override
    public int compareTo(DefaultConstraintMatchTotal<Score_> other) {
        return COMPARATOR.compare(this, other);
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
