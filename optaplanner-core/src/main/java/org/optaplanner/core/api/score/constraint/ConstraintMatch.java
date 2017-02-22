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
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.core.api.score.Score;

/**
 * Retrievable from {@link ConstraintMatchTotal#getConstraintMatchSet()}.
 */
public class ConstraintMatch implements Serializable, Comparable<ConstraintMatch> {

    protected final String constraintPackage;
    protected final String constraintName;

    protected final List<Object> justificationList;
    protected final Score score;

    /**
     * @param constraintPackage never null
     * @param constraintName never null
     * @param justificationList never null, sometimes empty
     * @param score never null
     */
    public ConstraintMatch(String constraintPackage, String constraintName,
            List<Object> justificationList, Score score) {
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.justificationList = justificationList;
        this.score = score;
    }

    public String getConstraintPackage() {
        return constraintPackage;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public List<Object> getJustificationList() {
        return justificationList;
    }

    public Score getScore() {
        return score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public String getConstraintId() {
        return constraintPackage + "/" + constraintName;
    }

    public String getIdentificationString() {
        return getConstraintId() + "/" + justificationList;
    }

    @Override
    public int compareTo(ConstraintMatch other) {
        return new CompareToBuilder()
                .append(getConstraintPackage(), other.getConstraintPackage())
                .append(getConstraintName(), other.getConstraintName())
                .append(getJustificationList(), other.getJustificationList())
                .append(getScore(), other.getScore())
                .toComparison();
    }

    @Override
    public String toString() {
        return getIdentificationString() + "=" + getScore();
    }

}
