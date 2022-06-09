package org.optaplanner.constraint.drl.holder;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;

class HardMediumSoftBigDecimalScoreHolderImplTest extends AbstractScoreHolderTest<HardMediumSoftBigDecimalScore> {

    @Test
    void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardMediumSoftBigDecimalScoreHolderImpl scoreHolder =
                new HardMediumSoftBigDecimalScoreHolderImpl(constraintMatchEnabled);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, new BigDecimal("-0.01"));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-0.01"), new BigDecimal("0.00"), new BigDecimal("0.00")));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, new BigDecimal("-0.08"));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-0.09"), new BigDecimal("0.00"), new BigDecimal("0.00")));
        callOnDelete(hard2Undo);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-0.01"), new BigDecimal("0.00"), new BigDecimal("0.00")));

        RuleContext medium1 = mockRuleContext("medium1");
        scoreHolder.addMediumConstraintMatch(medium1, new BigDecimal("-0.10"));
        callOnUpdate(medium1);
        scoreHolder.addMediumConstraintMatch(medium1, new BigDecimal("-0.20")); // Overwrite existing

        RuleContext soft1 = mockRuleContext("soft1", DEFAULT_JUSTIFICATION, OTHER_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft1, new BigDecimal("-1.00"));
        callOnUpdate(soft1);
        scoreHolder.addSoftConstraintMatch(soft1, new BigDecimal("-3.00")); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, new BigDecimal("-10.00"), new BigDecimal("-100.00"),
                new BigDecimal("-1000.00"));
        callOnUpdate(multi1);
        scoreHolder.addMultiConstraintMatch(multi1, new BigDecimal("-40.00"), new BigDecimal("-500.00"),
                new BigDecimal("-6000.00")); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, new BigDecimal("-10000.00"));
        callOnUpdate(hard3);
        scoreHolder.addHardConstraintMatch(hard3, new BigDecimal("-70000.00")); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo", UNDO_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft2Undo, new BigDecimal("-0.99"));
        callOnDelete(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, new BigDecimal("-9.99"), new BigDecimal("-9.99"),
                new BigDecimal("-9.99"));
        callOnDelete(multi2Undo);

        RuleContext medium2Undo = mockRuleContext("medium2Undo");
        scoreHolder.addMediumConstraintMatch(medium2Undo, new BigDecimal("-99.99"));
        callOnDelete(medium2Undo);

        assertThat(scoreHolder.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("-70040.01"), new BigDecimal("-500.20"),
                        new BigDecimal("-6003.00")));
        assertThat(scoreHolder.extractScore(-7)).isEqualTo(
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-70040.01"), new BigDecimal("-500.20"),
                        new BigDecimal("-6003.00")));
        if (constraintMatchEnabled) {
            assertThat(findConstraintMatchTotal(scoreHolder, "hard1").getScore())
                    .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("-0.01"),
                            new BigDecimal("0.00"), new BigDecimal("0.00")));
            assertThat(scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScore())
                    .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("0.00"),
                            new BigDecimal("0.00"), new BigDecimal("-3.00")));
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
        HardMediumSoftBigDecimalScoreHolderImpl scoreHolder =
                new HardMediumSoftBigDecimalScoreHolderImpl(constraintMatchEnabled);
        Rule hard1 = mockRule("hard1");
        scoreHolder.configureConstraintWeight(hard1, HardMediumSoftBigDecimalScore.ofHard(new BigDecimal("10.0")));
        Rule hard2 = mockRule("hard2");
        scoreHolder.configureConstraintWeight(hard2, HardMediumSoftBigDecimalScore.ofHard(new BigDecimal("100.0")));
        Rule medium1 = mockRule("medium1");
        scoreHolder.configureConstraintWeight(medium1, HardMediumSoftBigDecimalScore.ofMedium(new BigDecimal("10.0")));
        Rule soft1 = mockRule("soft1");
        scoreHolder.configureConstraintWeight(soft1, HardMediumSoftBigDecimalScore.ofSoft(new BigDecimal("10.0")));
        Rule soft2 = mockRule("soft2");
        scoreHolder.configureConstraintWeight(soft2, HardMediumSoftBigDecimalScore.ofSoft(new BigDecimal("100.0")));

        scoreHolder.penalize(mockRuleContext(hard1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("0.0"), new BigDecimal("0.0")));

        scoreHolder.penalize(mockRuleContext(hard2), new BigDecimal("2.0"));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-210.0"), new BigDecimal("0.0"), new BigDecimal("0.0")));

        scoreHolder.penalize(mockRuleContext(medium1), new BigDecimal("9.0"));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-210.0"), new BigDecimal("-90.0"), new BigDecimal("0.0")));

        scoreHolder.reward(mockRuleContext(soft1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-210.0"), new BigDecimal("-90.0"), new BigDecimal("10.0")));

        scoreHolder.reward(mockRuleContext(soft2), new BigDecimal("3.0"));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-210.0"), new BigDecimal("-90.0"), new BigDecimal("310.0")));
    }

}
