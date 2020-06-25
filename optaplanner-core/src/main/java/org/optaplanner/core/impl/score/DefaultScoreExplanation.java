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

package org.optaplanner.core.impl.score;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;

public final class DefaultScoreExplanation<Solution_> implements ScoreExplanation<Solution_> {

    private final Solution_ solution;
    private final Score score;
    private final String summary;
    private final Map<String, ConstraintMatchTotal> constraintMatchTotalMap;
    private final Map<Object, Indictment> indictmentMap;

    public DefaultScoreExplanation(Solution_ solution, Score score, String summary,
            Map<String, ConstraintMatchTotal> constraintMatchTotalMap, Map<Object, Indictment> indictmentMap) {
        this.solution = solution;
        this.score = requireNonNull(score);
        this.summary = requireNonNull(summary);
        this.constraintMatchTotalMap = requireNonNull(constraintMatchTotalMap);
        this.indictmentMap = requireNonNull(indictmentMap);
    }

    @Override
    public Solution_ getSolution() {
        return solution;
    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public Map<String, ConstraintMatchTotal> getConstraintMatchTotalMap() {
        return constraintMatchTotalMap;
    }

    @Override
    public Map<Object, Indictment> getIndictmentMap() {
        return indictmentMap;
    }

    @Override
    public String toString() {
        return summary; // So that this class can be used in strings directly.
    }
}
