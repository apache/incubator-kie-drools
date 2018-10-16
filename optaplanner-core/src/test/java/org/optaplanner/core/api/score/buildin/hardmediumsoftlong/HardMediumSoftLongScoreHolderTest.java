/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardmediumsoftlong;

import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class HardMediumSoftLongScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardMediumSoftLongScoreHolder scoreHolder = new HardMediumSoftLongScoreHolder(constraintMatchEnabled);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, -1L);
        assertEquals(HardMediumSoftLongScore.of(-1L, 0L, 0L), scoreHolder.extractScore(0));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, -8L);
        assertEquals(HardMediumSoftLongScore.of(-9L, 0L, 0L), scoreHolder.extractScore(0));
        callOnDelete(hard2Undo);
        assertEquals(HardMediumSoftLongScore.of(-1L, 0L, 0L), scoreHolder.extractScore(0));

        RuleContext medium1 = mockRuleContext("medium1");
        scoreHolder.addMediumConstraintMatch(medium1, -10L);
        callOnUpdate(medium1);
        scoreHolder.addMediumConstraintMatch(medium1, -20L); // Overwrite existing

        RuleContext soft1 = mockRuleContext("soft1", DEFAULT_JUSTIFICATION, OTHER_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft1, -100L);
        callOnUpdate(soft1);
        scoreHolder.addSoftConstraintMatch(soft1, -300L); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, -1000L, -10000L, -100000L);
        callOnUpdate(multi1);
        scoreHolder.addMultiConstraintMatch(multi1, -4000L, -50000L, -600000L); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, -1000000L);
        callOnUpdate(hard3);
        scoreHolder.addHardConstraintMatch(hard3, -7000000L); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo", UNDO_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft2Undo, -99L);
        callOnDelete(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, -999L, -999L, -999L);
        callOnDelete(multi2Undo);

        RuleContext medium2Undo = mockRuleContext("medium2Undo");
        scoreHolder.addMediumConstraintMatch(medium2Undo, -9999L);
        callOnDelete(medium2Undo);

        assertEquals(HardMediumSoftLongScore.of(-7004001L, -50020L, -600300L), scoreHolder.extractScore(0));
        assertEquals(HardMediumSoftLongScore.ofUninitialized(-7, -7004001L, -50020L, -600300L), scoreHolder.extractScore(-7));
        if (constraintMatchEnabled) {
            assertEquals(HardMediumSoftLongScore.of(-1L, 0L, 0L), findConstraintMatchTotal(scoreHolder, "hard1").getScore());
            assertEquals(HardMediumSoftLongScore.of(0L, 0L, -300L), scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScore());
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
        HardMediumSoftLongScoreHolder scoreHolder = new HardMediumSoftLongScoreHolder(constraintMatchEnabled);
        Rule hard1 = mockRule("hard1");
        scoreHolder.putConstraintWeight(hard1, HardMediumSoftLongScore.ofHard(10L));
        Rule hard2 = mockRule("hard2");
        scoreHolder.putConstraintWeight(hard2, HardMediumSoftLongScore.ofHard(100L));
        Rule medium1 = mockRule("medium1");
        scoreHolder.putConstraintWeight(medium1, HardMediumSoftLongScore.ofMedium(10L));
        Rule soft1 = mockRule("soft1");
        scoreHolder.putConstraintWeight(soft1, HardMediumSoftLongScore.ofSoft(10L));
        Rule soft2 = mockRule("soft2");
        scoreHolder.putConstraintWeight(soft2, HardMediumSoftLongScore.ofSoft(100L));

        scoreHolder.penalize(mockRuleContext(hard1));
        assertEquals(HardMediumSoftLongScore.of(-10L, 0L, 0L), scoreHolder.extractScore(0));

        scoreHolder.penalize(mockRuleContext(hard2), 2L);
        assertEquals(HardMediumSoftLongScore.of(-210L, 0L, 0L), scoreHolder.extractScore(0));

        scoreHolder.penalize(mockRuleContext(medium1), 9L);
        assertEquals(HardMediumSoftLongScore.of(-210L, -90L, 0L), scoreHolder.extractScore(0));

        scoreHolder.reward(mockRuleContext(soft1));
        assertEquals(HardMediumSoftLongScore.of(-210L, -90L, 10L), scoreHolder.extractScore(0));

        scoreHolder.reward(mockRuleContext(soft2), 3L);
        assertEquals(HardMediumSoftLongScore.of(-210L, -90L, 310L), scoreHolder.extractScore(0));
    }

}
