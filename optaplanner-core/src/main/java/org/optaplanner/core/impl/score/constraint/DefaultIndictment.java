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

import java.util.LinkedHashSet;
import java.util.Set;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.Indictment;

public final class DefaultIndictment implements Indictment {

    private final Object justification;

    private final Set<ConstraintMatch> constraintMatchSet;
    private Score score;

    public DefaultIndictment(Object justification, Score zeroScore) {
        this.justification = justification;
        constraintMatchSet = new LinkedHashSet<>();
        score = zeroScore;
    }

    @Override
    public Object getJustification() {
        return justification;
    }

    @Override
    public Set<ConstraintMatch> getConstraintMatchSet() {
        return constraintMatchSet;
    }

    @Override
    public Score getScore() {
        return score;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof DefaultIndictment) {
            DefaultIndictment other = (DefaultIndictment) o;
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
