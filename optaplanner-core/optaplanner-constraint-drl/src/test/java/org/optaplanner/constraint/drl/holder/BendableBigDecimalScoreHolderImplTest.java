package org.optaplanner.constraint.drl.holder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScoreHolder;

class BendableBigDecimalScoreHolderImplTest extends AbstractScoreHolderTest<BendableBigDecimalScore> {

    @Test
    void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        BendableBigDecimalScoreHolderImpl scoreHolder = new BendableBigDecimalScoreHolderImpl(constraintMatchEnabled, 1, 2);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, 0, new BigDecimal("-0.01"));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(BendableBigDecimalScore.of(new BigDecimal[] { new BigDecimal("-0.01") },
                        new BigDecimal[] { new BigDecimal("0.00"), new BigDecimal("0.00") }));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, 0, new BigDecimal("-0.08"));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(BendableBigDecimalScore.of(new BigDecimal[] { new BigDecimal("-0.09") },
                        new BigDecimal[] { new BigDecimal("0.00"), new BigDecimal("0.00") }));
        callOnDelete(hard2Undo);
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(BendableBigDecimalScore.of(new BigDecimal[] { new BigDecimal("-0.01") },
                        new BigDecimal[] { new BigDecimal("0.00"), new BigDecimal("0.00") }));

        RuleContext medium1 = mockRuleContext("medium1");
        scoreHolder.addSoftConstraintMatch(medium1, 0, new BigDecimal("-0.10"));
        callOnUpdate(medium1);
        scoreHolder.addSoftConstraintMatch(medium1, 0, new BigDecimal("-0.20")); // Overwrite existing

        RuleContext soft1 = mockRuleContext("soft1", DEFAULT_JUSTIFICATION, OTHER_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft1, 1, new BigDecimal("-1.00"));
        callOnUpdate(soft1);
        scoreHolder.addSoftConstraintMatch(soft1, 1, new BigDecimal("-3.00")); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, new BigDecimal[] { new BigDecimal("-10.00") },
                new BigDecimal[] { new BigDecimal("-100.00"), new BigDecimal("-1000.00") });
        callOnUpdate(multi1);
        scoreHolder.addMultiConstraintMatch(multi1, new BigDecimal[] { new BigDecimal("-40.00") },
                new BigDecimal[] { new BigDecimal("-500.00"), new BigDecimal("-6000.00") }); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, 0, new BigDecimal("-10000.00"));
        callOnUpdate(hard3);
        scoreHolder.addHardConstraintMatch(hard3, 0, new BigDecimal("-70000.00")); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo", UNDO_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft2Undo, 1, new BigDecimal("-0.99"));
        callOnDelete(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, new BigDecimal[] { new BigDecimal("-9.99") },
                new BigDecimal[] { new BigDecimal("-9.99"), new BigDecimal("-9.99") });
        callOnDelete(multi2Undo);

        RuleContext medium2Undo = mockRuleContext("medium2Undo");
        scoreHolder.addSoftConstraintMatch(medium2Undo, 0, new BigDecimal("-99.99"));
        callOnDelete(medium2Undo);

        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(BendableBigDecimalScore.of(new BigDecimal[] { new BigDecimal("-70040.01") },
                        new BigDecimal[] { new BigDecimal("-500.20"), new BigDecimal("-6003.00") }));
        assertThat(scoreHolder.extractScore(-7))
                .isEqualTo(BendableBigDecimalScore.ofUninitialized(-7, new BigDecimal[] { new BigDecimal("-70040.01") },
                        new BigDecimal[] { new BigDecimal("-500.20"), new BigDecimal("-6003.00") }));
        if (constraintMatchEnabled) {
            assertThat(findConstraintMatchTotal(scoreHolder, "hard1").getScore())
                    .isEqualTo(BendableBigDecimalScore.of(
                            new BigDecimal[] { new BigDecimal("-0.01") },
                            new BigDecimal[] { new BigDecimal("0.00"), new BigDecimal("0.00") }));
            assertThat(scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScore())
                    .isEqualTo(BendableBigDecimalScore.of(
                            new BigDecimal[] { new BigDecimal("0.00") },
                            new BigDecimal[] { new BigDecimal("0.00"), new BigDecimal("-3.00") }));
            assertThat(scoreHolder.getIndictmentMap().get(UNDO_JUSTIFICATION)).isNull();
        }
    }

    @Test
    void rewardPenalizeWithConstraintMatch() {
        rewardPenalize(true);
    }

    @Test
    void rewardPenalizeWithoutConstraintMatch() {
        rewardPenalize(false);
    }

    public void rewardPenalize(boolean constraintMatchEnabled) {
        BendableBigDecimalScoreHolderImpl scoreHolder = new BendableBigDecimalScoreHolderImpl(constraintMatchEnabled, 1, 2);
        Rule hard1 = mockRule("hard1");
        scoreHolder.configureConstraintWeight(hard1, BendableBigDecimalScore.ofHard(1, 2, 0, new BigDecimal("10.0")));
        Rule hard2 = mockRule("hard2");
        scoreHolder.configureConstraintWeight(hard2, BendableBigDecimalScore.ofHard(1, 2, 0, new BigDecimal("100.0")));
        Rule medium1 = mockRule("medium1");
        scoreHolder.configureConstraintWeight(medium1, BendableBigDecimalScore.ofSoft(1, 2, 0, new BigDecimal("10.0")));
        Rule soft1 = mockRule("soft1");
        scoreHolder.configureConstraintWeight(soft1, BendableBigDecimalScore.ofSoft(1, 2, 1, new BigDecimal("10.0")));
        Rule soft2 = mockRule("soft2");
        scoreHolder.configureConstraintWeight(soft2, BendableBigDecimalScore.ofSoft(1, 2, 1, new BigDecimal("100.0")));

        scoreHolder.penalize(mockRuleContext(hard1));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(BendableBigDecimalScore.of(new BigDecimal[] { new BigDecimal("-10.0") },
                        new BigDecimal[] { new BigDecimal("0.0"), new BigDecimal("0.0") }));

        scoreHolder.penalize(mockRuleContext(hard2), new BigDecimal("2.0"));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(BendableBigDecimalScore.of(new BigDecimal[] { new BigDecimal("-210.0") },
                        new BigDecimal[] { new BigDecimal("0.0"), new BigDecimal("0.0") }));

        scoreHolder.penalize(mockRuleContext(medium1), new BigDecimal("9.0"));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(BendableBigDecimalScore.of(new BigDecimal[] { new BigDecimal("-210.0") },
                        new BigDecimal[] { new BigDecimal("-90.0"), new BigDecimal("0.0") }));

        scoreHolder.reward(mockRuleContext(soft1));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(BendableBigDecimalScore.of(new BigDecimal[] { new BigDecimal("-210.0") },
                        new BigDecimal[] { new BigDecimal("-90.0"), new BigDecimal("10.0") }));

        scoreHolder.reward(mockRuleContext(soft2), new BigDecimal("3.0"));
        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(BendableBigDecimalScore.of(new BigDecimal[] { new BigDecimal("-210.0") },
                        new BigDecimal[] { new BigDecimal("-90.0"), new BigDecimal("310.0") }));
    }

    @Test
    void failFastHardLevel() {
        BendableBigDecimalScoreHolder scoreHolder = new BendableBigDecimalScoreHolderImpl(false, 2, 5);
        RuleContext rule = mockRuleContext("rule");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> scoreHolder.addHardConstraintMatch(rule, 3, new BigDecimal("-0.01")));
    }

    @Test
    void failFastSoftLevel() {
        BendableBigDecimalScoreHolder scoreHolder = new BendableBigDecimalScoreHolderImpl(false, 5, 2);
        RuleContext rule = mockRuleContext("rule");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> scoreHolder.addSoftConstraintMatch(rule, 3, new BigDecimal("-0.01")));
    }

}
