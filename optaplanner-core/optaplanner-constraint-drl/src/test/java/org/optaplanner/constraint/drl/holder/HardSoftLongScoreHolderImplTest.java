package org.optaplanner.constraint.drl.holder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

class HardSoftLongScoreHolderImplTest extends AbstractScoreHolderTest<HardSoftLongScore> {

    @Test
    void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardSoftLongScoreHolderImpl scoreHolder = new HardSoftLongScoreHolderImpl(constraintMatchEnabled);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, -1L);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftLongScore.of(-1L, -0L));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, -8L);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftLongScore.of(-9L, -0L));
        callOnDelete(hard2Undo);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftLongScore.of(-1L, -0L));

        RuleContext soft1 = mockRuleContext("soft1", DEFAULT_JUSTIFICATION, OTHER_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft1, -10L);
        callOnUpdate(soft1);
        scoreHolder.addSoftConstraintMatch(soft1, -20L); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, -100L, -1000L);
        callOnUpdate(multi1);
        scoreHolder.addMultiConstraintMatch(multi1, -300L, -4000L); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, -10000L);
        callOnUpdate(hard3);
        scoreHolder.addHardConstraintMatch(hard3, -50000L); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo", UNDO_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft2Undo, -99L);
        callOnDelete(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, -999L, -999L);
        callOnDelete(multi2Undo);

        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftLongScore.of(-50301L, -4020L));
        assertThat(scoreHolder.extractScore(-7)).isEqualTo(HardSoftLongScore.ofUninitialized(-7, -50301L, -4020L));
        if (constraintMatchEnabled) {
            assertThat(findConstraintMatchTotal(scoreHolder, "hard1").getScore())
                    .isEqualTo(HardSoftLongScore.of(-1L, 0L));
            assertThat(scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScore())
                    .isEqualTo(HardSoftLongScore.of(0L, -20L));
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
        HardSoftLongScoreHolderImpl scoreHolder = new HardSoftLongScoreHolderImpl(constraintMatchEnabled);
        Rule hard1 = mockRule("hard1");
        scoreHolder.configureConstraintWeight(hard1, HardSoftLongScore.ofHard(10L));
        Rule hard2 = mockRule("hard2");
        scoreHolder.configureConstraintWeight(hard2, HardSoftLongScore.ofHard(100L));
        Rule soft1 = mockRule("soft1");
        scoreHolder.configureConstraintWeight(soft1, HardSoftLongScore.ofSoft(10L));
        Rule soft2 = mockRule("soft2");
        scoreHolder.configureConstraintWeight(soft2, HardSoftLongScore.ofSoft(100L));

        scoreHolder.penalize(mockRuleContext(hard1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftLongScore.of(-10L, 0L));

        scoreHolder.penalize(mockRuleContext(hard2), 2L);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftLongScore.of(-210L, 0L));

        scoreHolder.reward(mockRuleContext(soft1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftLongScore.of(-210L, 10L));

        scoreHolder.reward(mockRuleContext(soft2), 3L);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftLongScore.of(-210L, 310L));
    }

}
