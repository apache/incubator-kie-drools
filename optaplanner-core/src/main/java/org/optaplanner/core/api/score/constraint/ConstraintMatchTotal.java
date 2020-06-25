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

package org.optaplanner.core.api.score.constraint;

import java.util.Set;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;

/**
 * Explains the {@link Score} of a {@link PlanningSolution}, from the opposite side than {@link Indictment}.
 * Retrievable from {@link ScoreExplanation#getConstraintMatchTotalMap()}.
 */
public interface ConstraintMatchTotal {

    /**
     * @param constraintPackage never null
     * @param constraintName never null
     * @return never null
     */
    static String composeConstraintId(String constraintPackage, String constraintName) {
        return constraintPackage + "/" + constraintName;
    }

    /**
     * @return never null
     */
    String getConstraintPackage();

    /**
     * @return never null
     */
    String getConstraintName();

    /**
     * The value of the {@link ConstraintWeight} annotated member of the {@link ConstraintConfiguration}.
     * It's independent to the state of the {@link PlanningVariable planning variables}.
     * Do not confuse with {@link #getScore()}.
     *
     * @return null if {@link ConstraintWeight} isn't used for this constraint
     */
    Score getConstraintWeight();

    /**
     * @return never null
     */
    Set<ConstraintMatch> getConstraintMatchSet();

    /**
     * @return {@code >= 0}
     */
    default int getConstraintMatchCount() {
        return getConstraintMatchSet().size();
    }

    /**
     * Sum of the {@link #getConstraintMatchSet()}'s {@link ConstraintMatch#getScore()}.
     *
     * @return never null
     */
    Score getScore();

    /**
     * To create a constraintId, use {@link #composeConstraintId(String, String)}.
     *
     * @return never null
     */
    String getConstraintId();

}
