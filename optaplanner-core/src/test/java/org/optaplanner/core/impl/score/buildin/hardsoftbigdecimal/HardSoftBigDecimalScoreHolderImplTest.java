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

package org.optaplanner.core.impl.score.buildin.hardsoftbigdecimal;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.impl.score.buildin.AbstractScoreHolderTest;

public class HardSoftBigDecimalScoreHolderImplTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardSoftBigDecimalScoreHolderImpl scoreHolder = new HardSoftBigDecimalScoreHolderImpl(constraintMatchEnabled);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, new BigDecimal("-0.01"));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-0.01"), new BigDecimal("0.00")));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, new BigDecimal("-0.08"));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-0.09"), new BigDecimal("0.00")));
        callOnDelete(hard2Undo);
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-0.01"), new BigDecimal("0.00")));

        RuleContext soft1 = mockRuleContext("soft1", DEFAULT_JUSTIFICATION, OTHER_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft1, new BigDecimal("-0.10"));
        callOnUpdate(soft1);
        scoreHolder.addSoftConstraintMatch(soft1, new BigDecimal("-0.20")); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, new BigDecimal("-1.00"), new BigDecimal("-10.00"));
        callOnUpdate(multi1);
        scoreHolder.addMultiConstraintMatch(multi1, new BigDecimal("-3.00"), new BigDecimal("-40.00")); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, new BigDecimal("-100.00"));
        callOnUpdate(hard3);
        scoreHolder.addHardConstraintMatch(hard3, new BigDecimal("-500.00")); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo", UNDO_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft2Undo, new BigDecimal("-0.99"));
        callOnDelete(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, new BigDecimal("-9.99"), new BigDecimal("-9.99"));
        callOnDelete(multi2Undo);

        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-503.01"), new BigDecimal("-40.20")));
        assertThat(scoreHolder.extractScore(-7))
                .isEqualTo(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-503.01"), new BigDecimal("-40.20")));
        if (constraintMatchEnabled) {
            assertThat(findConstraintMatchTotal(scoreHolder, "hard1").getScore())
                    .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-0.01"),
                            BigDecimal.ZERO));
            assertThat(scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScore())
                    .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.ZERO,
                            new BigDecimal("-0.20")));
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
        HardSoftBigDecimalScoreHolderImpl scoreHolder = new HardSoftBigDecimalScoreHolderImpl(constraintMatchEnabled);
        Rule hard1 = mockRule("hard1");
        scoreHolder.configureConstraintWeight(hard1, HardSoftBigDecimalScore.ofHard(new BigDecimal("10.0")));
        Rule hard2 = mockRule("hard2");
        scoreHolder.configureConstraintWeight(hard2, HardSoftBigDecimalScore.ofHard(new BigDecimal("100.0")));
        Rule soft1 = mockRule("soft1");
        scoreHolder.configureConstraintWeight(soft1, HardSoftBigDecimalScore.ofSoft(new BigDecimal("10.0")));
        Rule soft2 = mockRule("soft2");
        scoreHolder.configureConstraintWeight(soft2, HardSoftBigDecimalScore.ofSoft(new BigDecimal("100.0")));

        scoreHolder.penalize(mockRuleContext(hard1));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("0.0")));

        scoreHolder.penalize(mockRuleContext(hard2), new BigDecimal("2.0"));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-210.0"), new BigDecimal("0.0")));

        scoreHolder.reward(mockRuleContext(soft1));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-210.0"), new BigDecimal("10.0")));

        scoreHolder.reward(mockRuleContext(soft2), new BigDecimal("3.0"));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-210.0"), new BigDecimal("310.0")));
    }

}
