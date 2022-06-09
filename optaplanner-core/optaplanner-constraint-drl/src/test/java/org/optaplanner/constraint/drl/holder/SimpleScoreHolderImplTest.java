package org.optaplanner.constraint.drl.holder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

class SimpleScoreHolderImplTest extends AbstractScoreHolderTest<SimpleScore> {

    @Test
    void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        SimpleScoreHolderImpl scoreHolder = new SimpleScoreHolderImpl(constraintMatchEnabled);

        RuleContext scoreRule1 = mockRuleContext("scoreRule1");
        scoreHolder.addConstraintMatch(scoreRule1, -1000);

        RuleContext scoreRule2 = mockRuleContext("scoreRule2");
        scoreHolder.addConstraintMatch(scoreRule2, -200);
        callOnDelete(scoreRule2);

        RuleContext scoreRule3 = mockRuleContext("scoreRule3");
        scoreHolder.addConstraintMatch(scoreRule3, -30);
        callOnUpdate(scoreRule3);
        scoreHolder.addConstraintMatch(scoreRule3, -3); // Overwrite existing

        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleScore.ofUninitialized(0, -1003));
        assertThat(scoreHolder.extractScore(-7)).isEqualTo(SimpleScore.ofUninitialized(-7, -1003));
        if (constraintMatchEnabled) {
            assertThat(findConstraintMatchTotal(scoreHolder, "scoreRule1").getScore())
                    .isEqualTo(SimpleScore.of(-1000));
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
        SimpleScoreHolderImpl scoreHolder = new SimpleScoreHolderImpl(constraintMatchEnabled);
        Rule constraint1 = mockRule("constraint1");
        scoreHolder.configureConstraintWeight(constraint1, SimpleScore.of(10));
        Rule constraint2 = mockRule("constraint2");
        scoreHolder.configureConstraintWeight(constraint2, SimpleScore.of(100));

        scoreHolder.penalize(mockRuleContext(constraint1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleScore.of(-10));

        scoreHolder.penalize(mockRuleContext(constraint2), 2);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleScore.of(-210));

        scoreHolder = new SimpleScoreHolderImpl(constraintMatchEnabled);
        Rule constraint3 = mockRule("constraint3");
        scoreHolder.configureConstraintWeight(constraint3, SimpleScore.of(10));
        Rule constraint4 = mockRule("constraint4");
        scoreHolder.configureConstraintWeight(constraint4, SimpleScore.of(100));

        scoreHolder.reward(mockRuleContext(constraint3));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleScore.of(10));

        scoreHolder.reward(mockRuleContext(constraint4), 3);
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleScore.of(310));
    }

}
