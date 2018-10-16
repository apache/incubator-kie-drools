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

package org.optaplanner.core.api.score.buildin.hardsoft;

import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class HardSoftScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardSoftScoreHolder scoreHolder = new HardSoftScoreHolder(constraintMatchEnabled);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, -1);
        assertEquals(HardSoftScore.of(-1, 0), scoreHolder.extractScore(0));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, -8);
        assertEquals(HardSoftScore.of(-9, 0), scoreHolder.extractScore(0));
        callOnDelete(hard2Undo);
        assertEquals(HardSoftScore.of(-1, 0), scoreHolder.extractScore(0));

        RuleContext soft1 = mockRuleContext("soft1", DEFAULT_JUSTIFICATION, OTHER_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft1, -10);
        callOnUpdate(soft1);
        scoreHolder.addSoftConstraintMatch(soft1, -20); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, -100, -1000);
        callOnUpdate(multi1);
        scoreHolder.addMultiConstraintMatch(multi1, -300, -4000); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, -10000);
        callOnUpdate(hard3);
        scoreHolder.addHardConstraintMatch(hard3, -50000); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo", UNDO_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft2Undo, -99);
        callOnDelete(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, -999, -999);
        callOnDelete(multi2Undo);

        assertEquals(HardSoftScore.of(-50301, -4020), scoreHolder.extractScore(0));
        assertEquals(HardSoftScore.ofUninitialized(-7, -50301, -4020), scoreHolder.extractScore(-7));
        if (constraintMatchEnabled) {
            assertEquals(HardSoftScore.of(-1, 0), findConstraintMatchTotal(scoreHolder, "hard1").getScore());
            assertEquals(HardSoftScore.of(0, -20), scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScore());
            assertNull(scoreHolder.getIndictmentMap().get(UNDO_JUSTIFICATION));
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
        HardSoftScoreHolder scoreHolder = new HardSoftScoreHolder(constraintMatchEnabled);
        Rule hard1 = mockRule("hard1");
        scoreHolder.putConstraintWeight(hard1, HardSoftScore.ofHard(10));
        Rule hard2 = mockRule("hard2");
        scoreHolder.putConstraintWeight(hard2, HardSoftScore.ofHard(100));
        Rule soft1 = mockRule("soft1");
        scoreHolder.putConstraintWeight(soft1, HardSoftScore.ofSoft(10));
        Rule soft2 = mockRule("soft2");
        scoreHolder.putConstraintWeight(soft2, HardSoftScore.ofSoft(100));

        scoreHolder.penalize(mockRuleContext(hard1));
        assertEquals(HardSoftScore.of(-10, 0), scoreHolder.extractScore(0));

        scoreHolder.penalize(mockRuleContext(hard2), 2);
        assertEquals(HardSoftScore.of(-210, 0), scoreHolder.extractScore(0));

        scoreHolder.reward(mockRuleContext(soft1));
        assertEquals(HardSoftScore.of(-210, 10), scoreHolder.extractScore(0));

        scoreHolder.reward(mockRuleContext(soft2), 3);
        assertEquals(HardSoftScore.of(-210, 310), scoreHolder.extractScore(0));
    }

}
