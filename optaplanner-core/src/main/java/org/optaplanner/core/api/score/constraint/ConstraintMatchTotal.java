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

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Retrievable from {@link ScoreDirector#getConstraintMatchTotals()}.
 */
public class ConstraintMatchTotal implements Serializable, Comparable<ConstraintMatchTotal> {

    protected final String constraintPackage;
    protected final String constraintName;

    protected final Set<ConstraintMatch> constraintMatchSet;
    protected Score scoreTotal;

    /**
     * @param constraintPackage never null
     * @param constraintName never null
     * @param zeroScore never null
     */
    public ConstraintMatchTotal(String constraintPackage, String constraintName, Score zeroScore) {
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        constraintMatchSet = new LinkedHashSet<>();
        scoreTotal = zeroScore;
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
     * @return never null
     */
    public Score getScoreTotal() {
        return scoreTotal;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public ConstraintMatch addConstraintMatch(List<Object> justificationList, Score score) {
        scoreTotal = scoreTotal.add(score);
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
        scoreTotal = scoreTotal.subtract(constraintMatch.getScore());
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
        return new CompareToBuilder()
                .append(getConstraintPackage(), other.getConstraintPackage())
                .append(getConstraintName(), other.getConstraintName())
                .append(getScoreTotal(), other.getScoreTotal())
                .toComparison();
    }

    @Override
    public String toString() {
        return getConstraintId() + "=" + getScoreTotal();
    }

}
