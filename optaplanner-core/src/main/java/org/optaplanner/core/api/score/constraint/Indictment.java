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

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Retrievable from {@link ScoreDirector#getIndictmentMap()}.
 */
public class Indictment implements Serializable, Comparable<Indictment> {

    protected final Object justification;

    protected final Set<ConstraintMatch> constraintMatchSet;
    protected Score scoreTotal;

    /**
     * @param justification never null
     * @param zeroScore never null
     */
    public Indictment(Object justification, Score zeroScore) {
        this.justification = justification;
        constraintMatchSet = new LinkedHashSet<>();
        scoreTotal = zeroScore;
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
     * @return never null
     */
    public Score getScoreTotal() {
        return scoreTotal;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean addConstraintMatch(ConstraintMatch constraintMatch) {
        boolean added = constraintMatchSet.add(constraintMatch);
        if (added) {
            scoreTotal = scoreTotal.add(constraintMatch.getScore());
        }
        return added;
    }

    public void removeConstraintMatch(ConstraintMatch constraintMatch) {
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
        return new CompareToBuilder()
                .append(getScoreTotal(), other.getScoreTotal())
                // The justification might not implement Comparable - so we leave it in original order
                //.append(getJustification(), other.getJustification())
                .toComparison();
    }

    @Override
    public String toString() {
        return justification + "=" + getScoreTotal();
    }

}
