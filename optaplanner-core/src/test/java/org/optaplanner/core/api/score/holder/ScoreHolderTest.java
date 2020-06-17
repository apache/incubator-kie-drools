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
package org.optaplanner.core.api.score.holder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

public class ScoreHolderTest {

    @Test
    public void illegalStateExceptionThrownWhenConstraintMatchNotEnabled() {
        ScoreHolder scoreHolder = buildScoreHolder(false);
        assertThatIllegalStateException()
                .isThrownBy(scoreHolder::getConstraintMatchTotals)
                .withMessageContaining("constraintMatchEnabled");
    }

    @Test
    public void constraintMatchTotalsNeverNull() {
        assertThat(buildScoreHolder(true).getConstraintMatchTotals()).isNotNull();
    }

    private ScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new AbstractScoreHolder<SimpleScore>(constraintMatchEnabled, SimpleScore.ZERO) {
            @Override
            public SimpleScore extractScore(int initScore) {
                return SimpleScore.of(0);
            }

            @Override
            public void configureConstraintWeight(org.kie.api.definition.rule.Rule rule, SimpleScore constraintWeight) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
