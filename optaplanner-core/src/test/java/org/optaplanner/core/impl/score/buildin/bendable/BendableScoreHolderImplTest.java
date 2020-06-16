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

package org.optaplanner.core.impl.score.buildin.bendable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendable.BendableScoreHolder;
import org.optaplanner.core.impl.score.buildin.AbstractScoreHolderTest;

public class BendableScoreHolderImplTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        BendableScoreHolderImpl scoreHolder = new BendableScoreHolderImpl(constraintMatchEnabled, 1, 2);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, 0, -1);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -1 }, new int[] { 0, 0 }));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, 0, -8);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -9 }, new int[] { 0, 0 }));
        callOnDelete(hard2Undo);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -1 }, new int[] { 0, 0 }));

        RuleContext medium1 = mockRuleContext("medium1");
        scoreHolder.addSoftConstraintMatch(medium1, 0, -10);
        callOnUpdate(medium1);
        scoreHolder.addSoftConstraintMatch(medium1, 0, -20); // Overwrite existing

        RuleContext soft1 = mockRuleContext("soft1", DEFAULT_JUSTIFICATION, OTHER_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft1, 1, -100);
        callOnUpdate(soft1);
        scoreHolder.addSoftConstraintMatch(soft1, 1, -300); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, new int[] { -1000 }, new int[] { -10000, -100000 });
        callOnUpdate(multi1);
        scoreHolder.addMultiConstraintMatch(multi1, new int[] { -4000 }, new int[] { -50000, -600000 }); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, 0, -1000000);
        callOnUpdate(hard3);
        scoreHolder.addHardConstraintMatch(hard3, 0, -7000000); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo", UNDO_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft2Undo, 1, -99);
        callOnDelete(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, new int[] { -999 }, new int[] { -999, -999 });
        callOnDelete(multi2Undo);

        RuleContext medium2Undo = mockRuleContext("medium2Undo");
        scoreHolder.addSoftConstraintMatch(medium2Undo, 0, -9999);
        callOnDelete(medium2Undo);

        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(BendableScore.of(new int[] { -7004001 }, new int[] { -50020, -600300 }));
        assertThat(scoreHolder.extractScore(-7))
                .isEqualTo(BendableScore.ofUninitialized(-7, new int[] { -7004001 }, new int[] { -50020, -600300 }));
        if (constraintMatchEnabled) {
            assertThat(findConstraintMatchTotal(scoreHolder, "hard1").getScore())
                    .isEqualTo(BendableScore.of(new int[] { -1 }, new int[] { 0, 0 }));
            assertThat(scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScore())
                    .isEqualTo(BendableScore.of(new int[] { 0 }, new int[] { 0, -300 }));
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
        BendableScoreHolderImpl scoreHolder = new BendableScoreHolderImpl(constraintMatchEnabled, 1, 2);
        Rule hard1 = mockRule("hard1");
        scoreHolder.configureConstraintWeight(hard1, BendableScore.ofHard(1, 2, 0, 10));
        Rule hard2 = mockRule("hard2");
        scoreHolder.configureConstraintWeight(hard2, BendableScore.ofHard(1, 2, 0, 100));
        Rule medium1 = mockRule("medium1");
        scoreHolder.configureConstraintWeight(medium1, BendableScore.ofSoft(1, 2, 0, 10));
        Rule soft1 = mockRule("soft1");
        scoreHolder.configureConstraintWeight(soft1, BendableScore.ofSoft(1, 2, 1, 10));
        Rule soft2 = mockRule("soft2");
        scoreHolder.configureConstraintWeight(soft2, BendableScore.ofSoft(1, 2, 1, 100));

        scoreHolder.penalize(mockRuleContext(hard1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -10 }, new int[] { 0, 0 }));

        scoreHolder.penalize(mockRuleContext(hard2), 2);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -210 }, new int[] { 0, 0 }));

        scoreHolder.penalize(mockRuleContext(medium1), 9);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -210 }, new int[] { -90, 0 }));

        scoreHolder.reward(mockRuleContext(soft1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -210 }, new int[] { -90, 10 }));

        scoreHolder.reward(mockRuleContext(soft2), 3);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -210 }, new int[] { -90, 310 }));
    }

    @Test
    public void failFastHardLevel() {
        BendableScoreHolder scoreHolder = new BendableScoreHolderImpl(false, 2, 5);
        RuleContext rule = mockRuleContext("rule");
        assertThatIllegalArgumentException().isThrownBy(() -> scoreHolder.addHardConstraintMatch(rule, 3, -1));
    }

    @Test
    public void failFastSoftLevel() {
        BendableScoreHolder scoreHolder = new BendableScoreHolderImpl(false, 5, 2);
        RuleContext rule = mockRuleContext("rule");
        assertThatIllegalArgumentException().isThrownBy(() -> scoreHolder.addSoftConstraintMatch(rule, 3, -1));
    }

}
