package org.optaplanner.constraint.drl.holder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;

class HardMediumSoftLongScoreHolderImplTest extends AbstractScoreHolderTest<HardMediumSoftLongScore> {

    @Test
    void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardMediumSoftLongScoreHolderImpl scoreHolder = new HardMediumSoftLongScoreHolderImpl(constraintMatchEnabled);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, -1L);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-1L, 0L, 0L));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, -8L);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-9L, 0L, 0L));
        callOnDelete(hard2Undo);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-1L, 0L, 0L));

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

        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-7004001L, -50020L, -600300L));
        assertThat(scoreHolder.extractScore(-7))
                .isEqualTo(HardMediumSoftLongScore.ofUninitialized(-7, -7004001L, -50020L, -600300L));
        if (constraintMatchEnabled) {
            assertThat(findConstraintMatchTotal(scoreHolder, "hard1").getScore())
                    .isEqualTo(HardMediumSoftLongScore.of(-1L, 0L, 0L));
            assertThat(scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScore())
                    .isEqualTo(HardMediumSoftLongScore.of(0L, 0L, -300L));
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
        HardMediumSoftLongScoreHolderImpl scoreHolder = new HardMediumSoftLongScoreHolderImpl(constraintMatchEnabled);
        Rule hard1 = mockRule("hard1");
        scoreHolder.configureConstraintWeight(hard1, HardMediumSoftLongScore.ofHard(10L));
        Rule hard2 = mockRule("hard2");
        scoreHolder.configureConstraintWeight(hard2, HardMediumSoftLongScore.ofHard(100L));
        Rule medium1 = mockRule("medium1");
        scoreHolder.configureConstraintWeight(medium1, HardMediumSoftLongScore.ofMedium(10L));
        Rule soft1 = mockRule("soft1");
        scoreHolder.configureConstraintWeight(soft1, HardMediumSoftLongScore.ofSoft(10L));
        Rule soft2 = mockRule("soft2");
        scoreHolder.configureConstraintWeight(soft2, HardMediumSoftLongScore.ofSoft(100L));

        scoreHolder.penalize(mockRuleContext(hard1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-10L, 0L, 0L));

        scoreHolder.penalize(mockRuleContext(hard2), 2L);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-210L, 0L, 0L));

        scoreHolder.penalize(mockRuleContext(medium1), 9L);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-210L, -90L, 0L));

        scoreHolder.reward(mockRuleContext(soft1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-210L, -90L, 10L));

        scoreHolder.reward(mockRuleContext(soft2), 3L);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-210L, -90L, 310L));
    }

}
