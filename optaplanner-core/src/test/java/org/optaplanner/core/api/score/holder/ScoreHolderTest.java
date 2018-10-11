/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.api.score.holder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

import static org.junit.Assert.*;

public class ScoreHolderTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void illegalStateExceptionThrownWhenConstraintMatchNotEnabled() {
        ScoreHolder scoreHolder = buildScoreHolder(false);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("constraintMatchEnabled");
        scoreHolder.getConstraintMatchTotals().clear();
    }

    @Test
    public void constraintMatchTotalsNeverNull() {
        assertNotNull(buildScoreHolder(true).getConstraintMatchTotals());
    }

    private ScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new AbstractScoreHolder(constraintMatchEnabled, SimpleScore.ZERO) {
            @Override
            public Score<?> extractScore(int initScore) {
                return SimpleScore.of(0);
            }

            @Override
            public void putConstraintWeight(org.kie.api.definition.rule.Rule rule, Score constraintWeight) {
                throw new UnsupportedOperationException();
            }
        };
    }

}
