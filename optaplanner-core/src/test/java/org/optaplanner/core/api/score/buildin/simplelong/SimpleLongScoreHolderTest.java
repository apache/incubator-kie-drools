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

package org.optaplanner.core.api.score.buildin.simplelong;

import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class SimpleLongScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        SimpleLongScoreHolder scoreHolder = new SimpleLongScoreHolder(constraintMatchEnabled);

        RuleContext scoreRule1 = mockRuleContext("scoreRule1");
        scoreHolder.addConstraintMatch(scoreRule1, -1000L);

        RuleContext scoreRule2 = mockRuleContext("scoreRule2");
        scoreHolder.addConstraintMatch(scoreRule2, -200L);
        callOnDelete(scoreRule2);

        RuleContext scoreRule3 = mockRuleContext("scoreRule3");
        scoreHolder.addConstraintMatch(scoreRule3, -30L);
        callOnUpdate(scoreRule3);
        scoreHolder.addConstraintMatch(scoreRule3, -3L); // Overwrite existing

        assertEquals(SimpleLongScore.ofUninitialized(0, -1003L), scoreHolder.extractScore(0));
        assertEquals(SimpleLongScore.ofUninitialized(-7, -1003L), scoreHolder.extractScore(-7));
        if (constraintMatchEnabled) {
            assertEquals(SimpleLongScore.of(-1000L), findConstraintMatchTotal(scoreHolder, "scoreRule1").getScore());
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
        SimpleLongScoreHolder scoreHolder = new SimpleLongScoreHolder(constraintMatchEnabled);
        Rule constraint1 = mockRule("constraint1");
        scoreHolder.putConstraintWeight(constraint1, SimpleLongScore.of(10L));
        Rule constraint2 = mockRule("constraint2");
        scoreHolder.putConstraintWeight(constraint2, SimpleLongScore.of(100L));

        scoreHolder.penalize(mockRuleContext(constraint1));
        assertEquals(SimpleLongScore.of(-10L), scoreHolder.extractScore(0));

        scoreHolder.penalize(mockRuleContext(constraint2), 2L);
        assertEquals(SimpleLongScore.of(-210L), scoreHolder.extractScore(0));

        scoreHolder = new SimpleLongScoreHolder(constraintMatchEnabled);
        Rule constraint3 = mockRule("constraint3");
        scoreHolder.putConstraintWeight(constraint3, SimpleLongScore.of(10L));
        Rule constraint4 = mockRule("constraint4");
        scoreHolder.putConstraintWeight(constraint4, SimpleLongScore.of(100L));

        scoreHolder.reward(mockRuleContext(constraint3));
        assertEquals(SimpleLongScore.of(10L), scoreHolder.extractScore(0));

        scoreHolder.reward(mockRuleContext(constraint4), 3L);
        assertEquals(SimpleLongScore.of(310L), scoreHolder.extractScore(0));
    }

}
