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

package org.optaplanner.core.impl.score.buildin.hardmediumsoft;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.impl.score.buildin.AbstractScoreHolderTest;

public class HardMediumSoftScoreHolderImplTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardMediumSoftScoreHolderImpl scoreHolder = new HardMediumSoftScoreHolderImpl(constraintMatchEnabled);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, -1);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftScore.of(-1, 0, 0));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, -8);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftScore.of(-9, 0, 0));
        callOnDelete(hard2Undo);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftScore.of(-1, 0, 0));

        RuleContext medium1 = mockRuleContext("medium1");
        scoreHolder.addMediumConstraintMatch(medium1, -10);
        callOnUpdate(medium1);
        scoreHolder.addMediumConstraintMatch(medium1, -20); // Overwrite existing

        RuleContext soft1 = mockRuleContext("soft1", DEFAULT_JUSTIFICATION, OTHER_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft1, -100);
        callOnUpdate(soft1);
        scoreHolder.addSoftConstraintMatch(soft1, -300); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, -1000, -10000, -100000);
        callOnUpdate(multi1);
        scoreHolder.addMultiConstraintMatch(multi1, -4000, -50000, -600000); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, -1000000);
        callOnUpdate(hard3);
        scoreHolder.addHardConstraintMatch(hard3, -7000000); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo", UNDO_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft2Undo, -99);
        callOnDelete(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, -999, -999, -999);
        callOnDelete(multi2Undo);

        RuleContext medium2Undo = mockRuleContext("medium2Undo");
        scoreHolder.addMediumConstraintMatch(medium2Undo, -9999);
        callOnDelete(medium2Undo);

        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftScore.of(-7004001, -50020, -600300));
        assertThat(scoreHolder.extractScore(-7)).isEqualTo(HardMediumSoftScore.ofUninitialized(-7, -7004001, -50020, -600300));
        if (constraintMatchEnabled) {
            assertThat(findConstraintMatchTotal(scoreHolder, "hard1").getScore())
                    .isEqualTo(HardMediumSoftScore.of(-1, 0, 0));
            assertThat(scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScore())
                    .isEqualTo(HardMediumSoftScore.of(0, 0, -300));
            assertThat(scoreHolder.getIndictmentMap().get(UNDO_JUSTIFICATION)).isNull();
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
        HardMediumSoftScoreHolderImpl scoreHolder = new HardMediumSoftScoreHolderImpl(constraintMatchEnabled);
        Rule hard1 = mockRule("hard1");
        scoreHolder.configureConstraintWeight(hard1, HardMediumSoftScore.ofHard(10));
        Rule hard2 = mockRule("hard2");
        scoreHolder.configureConstraintWeight(hard2, HardMediumSoftScore.ofHard(100));
        Rule medium1 = mockRule("medium1");
        scoreHolder.configureConstraintWeight(medium1, HardMediumSoftScore.ofMedium(10));
        Rule soft1 = mockRule("soft1");
        scoreHolder.configureConstraintWeight(soft1, HardMediumSoftScore.ofSoft(10));
        Rule soft2 = mockRule("soft2");
        scoreHolder.configureConstraintWeight(soft2, HardMediumSoftScore.ofSoft(100));

        scoreHolder.penalize(mockRuleContext(hard1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftScore.of(-10, 0, 0));

        scoreHolder.penalize(mockRuleContext(hard2), 2);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftScore.of(-210, 0, 0));

        scoreHolder.penalize(mockRuleContext(medium1), 9);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftScore.of(-210, -90, 0));

        scoreHolder.reward(mockRuleContext(soft1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftScore.of(-210, -90, 10));

        scoreHolder.reward(mockRuleContext(soft2), 3);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftScore.of(-210, -90, 310));
    }

}
