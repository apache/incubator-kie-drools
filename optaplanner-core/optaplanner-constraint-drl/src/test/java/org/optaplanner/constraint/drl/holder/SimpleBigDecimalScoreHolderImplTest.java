package org.optaplanner.constraint.drl.holder;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;

class SimpleBigDecimalScoreHolderImplTest extends AbstractScoreHolderTest<SimpleBigDecimalScore> {

    @Test
    void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        SimpleBigDecimalScoreHolderImpl scoreHolder = new SimpleBigDecimalScoreHolderImpl(constraintMatchEnabled);

        RuleContext scoreRule1 = mockRuleContext("scoreRule1");
        scoreHolder.addConstraintMatch(scoreRule1, new BigDecimal("-10.00"));

        RuleContext scoreRule2 = mockRuleContext("scoreRule2");
        scoreHolder.addConstraintMatch(scoreRule2, new BigDecimal("-2.00"));
        callOnDelete(scoreRule2);

        RuleContext scoreRule3 = mockRuleContext("scoreRule3");
        scoreHolder.addConstraintMatch(scoreRule3, new BigDecimal("-0.30"));
        callOnUpdate(scoreRule3);
        scoreHolder.addConstraintMatch(scoreRule3, new BigDecimal("-0.03")); // Overwrite existing

        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleBigDecimalScore.ofUninitialized(0, new BigDecimal("-10.03")));
        assertThat(scoreHolder.extractScore(-7)).isEqualTo(SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.03")));
        if (constraintMatchEnabled) {
            assertThat(findConstraintMatchTotal(scoreHolder, "scoreRule1").getScore())
                    .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("-10.00")));
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
        SimpleBigDecimalScoreHolderImpl scoreHolder = new SimpleBigDecimalScoreHolderImpl(constraintMatchEnabled);
        Rule constraint1 = mockRule("constraint1");
        scoreHolder.configureConstraintWeight(constraint1, SimpleBigDecimalScore.of(new BigDecimal("10.0")));
        Rule constraint2 = mockRule("constraint2");
        scoreHolder.configureConstraintWeight(constraint2, SimpleBigDecimalScore.of(new BigDecimal("100.0")));

        scoreHolder.penalize(mockRuleContext(constraint1));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("-10.0")));

        scoreHolder.penalize(mockRuleContext(constraint2), new BigDecimal("2.0"));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("-210.0")));

        scoreHolder = new SimpleBigDecimalScoreHolderImpl(constraintMatchEnabled);
        Rule constraint3 = mockRule("constraint3");
        scoreHolder.configureConstraintWeight(constraint3, SimpleBigDecimalScore.of(new BigDecimal("10.0")));
        Rule constraint4 = mockRule("constraint4");
        scoreHolder.configureConstraintWeight(constraint4, SimpleBigDecimalScore.of(new BigDecimal("100.0")));

        scoreHolder.reward(mockRuleContext(constraint3));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("10.0")));

        scoreHolder.reward(mockRuleContext(constraint4), new BigDecimal("3.0"));
        assertThat(scoreHolder.extractScore(0)).isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("310.0")));
    }

}
