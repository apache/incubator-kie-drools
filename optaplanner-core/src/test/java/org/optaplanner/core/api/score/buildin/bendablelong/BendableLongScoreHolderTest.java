/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.score.buildin.bendablelong;

import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class BendableLongScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        BendableLongScoreHolder scoreHolder = new BendableLongScoreHolder(constraintMatchEnabled, 1, 2);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, 0, -1L);
        assertEquals(BendableLongScore.of(new long[]{-1L}, new long[]{0L, 0L}), scoreHolder.extractScore(0));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, 0, -8);
        assertEquals(BendableLongScore.of(new long[]{-9L}, new long[]{0L, 0L}), scoreHolder.extractScore(0));
        callOnDelete(hard2Undo);
        assertEquals(BendableLongScore.of(new long[]{-1L}, new long[]{0L, 0L}), scoreHolder.extractScore(0));

        RuleContext medium1 = mockRuleContext("medium1");
        scoreHolder.addSoftConstraintMatch(medium1, 0, -10L);
        callOnUpdate(medium1);
        scoreHolder.addSoftConstraintMatch(medium1, 0, -20L); // Overwrite existing

        RuleContext soft1 = mockRuleContext("soft1", DEFAULT_JUSTIFICATION, OTHER_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft1, 1, -100L);
        callOnUpdate(soft1);
        scoreHolder.addSoftConstraintMatch(soft1, 1, -300L); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, new long[]{-1000L}, new long[]{-10000L, -100000L});
        callOnUpdate(multi1);
        scoreHolder.addMultiConstraintMatch(multi1, new long[]{-4000L}, new long[]{-50000L, -600000L}); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, 0, -1000000L);
        callOnUpdate(hard3);
        scoreHolder.addHardConstraintMatch(hard3, 0, -7000000L); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo", UNDO_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft2Undo, 1, -99L);
        callOnDelete(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, new long[]{-999L}, new long[]{-999L, -999L});
        callOnDelete(multi2Undo);

        RuleContext medium2Undo = mockRuleContext("medium2Undo");
        scoreHolder.addSoftConstraintMatch(medium2Undo, 0, -9999L);
        callOnDelete(medium2Undo);

        assertEquals(BendableLongScore.of(new long[]{-7004001L}, new long[]{-50020L, -600300L}), scoreHolder.extractScore(0));
        assertEquals(BendableLongScore.ofUninitialized(-7, new long[]{-7004001L}, new long[]{-50020L, -600300L}), scoreHolder.extractScore(-7));
        if (constraintMatchEnabled) {
            assertEquals(BendableLongScore.of(new long[]{-1L}, new long[]{0L, 0L}), findConstraintMatchTotal(scoreHolder, "hard1").getScore());
            assertEquals(BendableLongScore.of(new long[]{0L}, new long[]{0L, -300L}), scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScore());
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
        BendableLongScoreHolder scoreHolder = new BendableLongScoreHolder(constraintMatchEnabled, 1, 2);
        Rule hard1 = mockRule("hard1");
        scoreHolder.putConstraintWeight(hard1, BendableLongScore.ofHard(1, 2, 0, 10L));
        Rule hard2 = mockRule("hard2");
        scoreHolder.putConstraintWeight(hard2, BendableLongScore.ofHard(1, 2, 0, 100L));
        Rule medium1 = mockRule("medium1");
        scoreHolder.putConstraintWeight(medium1, BendableLongScore.ofSoft(1, 2, 0, 10L));
        Rule soft1 = mockRule("soft1");
        scoreHolder.putConstraintWeight(soft1, BendableLongScore.ofSoft(1, 2, 1, 10L));
        Rule soft2 = mockRule("soft2");
        scoreHolder.putConstraintWeight(soft2, BendableLongScore.ofSoft(1, 2, 1, 100L));

        scoreHolder.penalize(mockRuleContext(hard1));
        assertEquals(BendableLongScore.of(new long[]{-10L}, new long[]{0L, 0L}), scoreHolder.extractScore(0));

        scoreHolder.penalize(mockRuleContext(hard2), 2L);
        assertEquals(BendableLongScore.of(new long[]{-210L}, new long[]{0L, 0L}), scoreHolder.extractScore(0));

        scoreHolder.penalize(mockRuleContext(medium1), 9L);
        assertEquals(BendableLongScore.of(new long[]{-210L}, new long[]{-90L, 0L}), scoreHolder.extractScore(0));

        scoreHolder.reward(mockRuleContext(soft1));
        assertEquals(BendableLongScore.of(new long[]{-210L}, new long[]{-90L, 10L}), scoreHolder.extractScore(0));

        scoreHolder.reward(mockRuleContext(soft2), 3L);
        assertEquals(BendableLongScore.of(new long[]{-210L}, new long[]{-90L, 310L}), scoreHolder.extractScore(0));
    }

}
