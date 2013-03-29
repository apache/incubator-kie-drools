/*
 * Copyright 2013 JBoss Inc
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

public abstract class ScoreConstraintMatchTotal {

    protected String constraintPackage;
    protected String constraintName;
    protected int scoreLevel;

    protected ScoreConstraintMatchTotal(String constraintPackage, String constraintName, int scoreLevel) {
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.scoreLevel = scoreLevel;
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

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public String toString() {
        return constraintPackage + "/" + constraintName + "/" + scoreLevel;
    }

}
