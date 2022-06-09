package org.optaplanner.constraint.drl.holder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;

class SimpleLongScoreHolderImplTest extends AbstractScoreHolderTest<SimpleLongScore> {

    @Test
    void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        SimpleLongScoreHolderImpl scoreHolder = new SimpleLongScoreHolderImpl(constraintMatchEnabled);

        RuleContext scoreRule1 = mockRuleContext("scoreRule1");
        scoreHolder.addConstraintMatch(scoreRule1, -1000L);

        RuleContext scoreRule2 = mockRuleContext("scoreRule2");
        scoreHolder.addConstraintMatch(scoreRule2, -200L);
        callOnDelete(scoreRule2);

        RuleContext scoreRule3 = mockRuleContext("scoreRule3");
        scoreHolder.addConstraintMatch(scoreRule3, -30L);
        callOnUpdate(scoreRule3);
        scoreHolder.addConstraintMatch(scoreRule3, -3L); // Overwrite existing

        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleLongScore.ofUninitialized(0, -1003L));
        assertThat(scoreHolder.extractScore(-7)).isEqualTo(SimpleLongScore.ofUninitialized(-7, -1003L));
        if (constraintMatchEnabled) {
            assertThat(findConstraintMatchTotal(scoreHolder, "scoreRule1").getScore())
                    .isEqualTo(SimpleLongScore.of(-1000L));
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
        SimpleLongScoreHolderImpl scoreHolder = new SimpleLongScoreHolderImpl(constraintMatchEnabled);
        Rule constraint1 = mockRule("constraint1");
        scoreHolder.configureConstraintWeight(constraint1, SimpleLongScore.of(10L));
        Rule constraint2 = mockRule("constraint2");
        scoreHolder.configureConstraintWeight(constraint2, SimpleLongScore.of(100L));

        scoreHolder.penalize(mockRuleContext(constraint1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleLongScore.of(-10L));

        scoreHolder.penalize(mockRuleContext(constraint2), 2L);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleLongScore.of(-210L));

        scoreHolder = new SimpleLongScoreHolderImpl(constraintMatchEnabled);
        Rule constraint3 = mockRule("constraint3");
        scoreHolder.configureConstraintWeight(constraint3, SimpleLongScore.of(10L));
        Rule constraint4 = mockRule("constraint4");
        scoreHolder.configureConstraintWeight(constraint4, SimpleLongScore.of(100L));

        scoreHolder.reward(mockRuleContext(constraint3));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleLongScore.of(10L));

        scoreHolder.reward(mockRuleContext(constraint4), 3L);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleLongScore.of(310L));
    }

}
