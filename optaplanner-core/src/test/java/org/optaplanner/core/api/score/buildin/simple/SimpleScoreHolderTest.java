/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.simple;

import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class SimpleScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        SimpleScoreHolder scoreHolder = new SimpleScoreHolder(constraintMatchEnabled);

        RuleContext scoreRule1 = mockRuleContext("scoreRule1");
        scoreHolder.addConstraintMatch(scoreRule1, -1000);

        RuleContext scoreRule2 = mockRuleContext("scoreRule2");
        scoreHolder.addConstraintMatch(scoreRule2, -200);
        callOnDelete(scoreRule2);

        RuleContext scoreRule3 = mockRuleContext("scoreRule3");
        scoreHolder.addConstraintMatch(scoreRule3, -30);
        callOnUpdate(scoreRule3);
        scoreHolder.addConstraintMatch(scoreRule3, -3); // Overwrite existing

        assertEquals(SimpleScore.ofUninitialized(0, -1003), scoreHolder.extractScore(0));
        assertEquals(SimpleScore.ofUninitialized(-7, -1003), scoreHolder.extractScore(-7));
        if (constraintMatchEnabled) {
            assertEquals(SimpleScore.of(-1000), findConstraintMatchTotal(scoreHolder, "scoreRule1").getScore());
        }
    }

    @Test
    public void rewardPenalizeWithConstraintMatch() {
        rewardPenalize(true);
    }

    @Test
    public void rewardPenalizeWithoutConstraintMatch() {
        rewardPenalize(false);
    }

    public void rewardPenalize(boolean constraintMatchEnabled) {
        SimpleScoreHolder scoreHolder = new SimpleScoreHolder(constraintMatchEnabled);
        Rule constraint1 = mockRule("constraint1");
        scoreHolder.putConstraintWeight(constraint1, SimpleScore.of(10));
        Rule constraint2 = mockRule("constraint2");
        scoreHolder.putConstraintWeight(constraint2, SimpleScore.of(100));

        scoreHolder.penalize(mockRuleContext(constraint1));
        assertEquals(SimpleScore.of(-10), scoreHolder.extractScore(0));

        scoreHolder.penalize(mockRuleContext(constraint2), 2);
        assertEquals(SimpleScore.of(-210), scoreHolder.extractScore(0));

        scoreHolder = new SimpleScoreHolder(constraintMatchEnabled);
        Rule constraint3 = mockRule("constraint3");
        scoreHolder.putConstraintWeight(constraint3, SimpleScore.of(10));
        Rule constraint4 = mockRule("constraint4");
        scoreHolder.putConstraintWeight(constraint4, SimpleScore.of(100));

        scoreHolder.reward(mockRuleContext(constraint3));
        assertEquals(SimpleScore.of(10), scoreHolder.extractScore(0));

        scoreHolder.reward(mockRuleContext(constraint4), 3);
        assertEquals(SimpleScore.of(310), scoreHolder.extractScore(0));
    }

}
