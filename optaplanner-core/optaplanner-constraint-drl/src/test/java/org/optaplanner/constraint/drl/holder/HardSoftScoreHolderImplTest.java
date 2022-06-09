package org.optaplanner.constraint.drl.holder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

class HardSoftScoreHolderImplTest extends AbstractScoreHolderTest<HardSoftScore> {

    @Test
    void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardSoftScoreHolderImpl scoreHolder = new HardSoftScoreHolderImpl(constraintMatchEnabled);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, -1);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftScore.of(-1, 0));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, -8);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftScore.of(-9, 0));
        callOnDelete(hard2Undo);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftScore.of(-1, 0));

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

        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftScore.of(-50301, -4020));
        assertThat(scoreHolder.extractScore(-7)).isEqualTo(HardSoftScore.ofUninitialized(-7, -50301, -4020));
        if (constraintMatchEnabled) {
            assertThat(findConstraintMatchTotal(scoreHolder, "hard1").getScore())
                    .isEqualTo(HardSoftScore.of(-1, 0));
            assertThat(scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScore())
                    .isEqualTo(HardSoftScore.of(0, -20));
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
        HardSoftScoreHolderImpl scoreHolder = new HardSoftScoreHolderImpl(constraintMatchEnabled);
        Rule hard1 = mockRule("hard1");
        scoreHolder.configureConstraintWeight(hard1, HardSoftScore.ofHard(10));
        Rule hard2 = mockRule("hard2");
        scoreHolder.configureConstraintWeight(hard2, HardSoftScore.ofHard(100));
        Rule soft1 = mockRule("soft1");
        scoreHolder.configureConstraintWeight(soft1, HardSoftScore.ofSoft(10));
        Rule soft2 = mockRule("soft2");
        scoreHolder.configureConstraintWeight(soft2, HardSoftScore.ofSoft(100));

        scoreHolder.penalize(mockRuleContext(hard1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftScore.of(-10, 0));

        scoreHolder.penalize(mockRuleContext(hard2), 2);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftScore.of(-210, 0));

        scoreHolder.reward(mockRuleContext(soft1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftScore.of(-210, 10));

        scoreHolder.reward(mockRuleContext(soft2), 3);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(HardSoftScore.of(-210, 310));
    }

}
