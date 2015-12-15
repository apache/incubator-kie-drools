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

/**
 * Retrievable from {@link ConstraintMatchTotal#getConstraintMatchSet()}.
 */
public abstract class ConstraintMatch implements Serializable, Comparable<ConstraintMatch> {

    protected final String constraintPackage;
    protected final String constraintName;
    protected final int scoreLevel;

    protected final List<Object> justificationList;

    protected ConstraintMatch(String constraintPackage, String constraintName, int scoreLevel,
            List<Object> justificationList) {
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.scoreLevel = scoreLevel;
        this.justificationList = justificationList;
    }

    public String getConstraintPackage() {
        return constraintPackage;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public int getScoreLevel() {
        return scoreLevel;
    }

    public List<Object> getJustificationList() {
        return justificationList;
    }

    public abstract Number getWeightAsNumber();

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public String getIdentificationString() {
        return constraintPackage + "/" + constraintName + "/level" + scoreLevel + "/" + justificationList;
    }

    @Override
    public int compareTo(ConstraintMatch other) {
        return new CompareToBuilder()
                .append(getConstraintPackage(), other.getConstraintPackage())
                .append(getConstraintName(), other.getConstraintName())
                .append(getScoreLevel(), other.getScoreLevel())
                .append(getJustificationList(), other.getJustificationList())
                .append(getWeightAsNumber(), other.getWeightAsNumber())
                .toComparison();
    }

    public String toString() {
        return getIdentificationString()  + "=" + getWeightAsNumber();
    }

}
