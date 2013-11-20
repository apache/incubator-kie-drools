package org.optaplanner.core.api.score.buildin.simpledouble;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class SimpleDoubleScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        SimpleDoubleScoreHolder scoreHolder = new SimpleDoubleScoreHolder(constraintMatchEnabled);

        scoreHolder.addConstraintMatch(createRuleContext("scoreRule1"), -10.00); // Rule match added

        RuleContext ruleContext2 = createRuleContext("scoreRule2");
        scoreHolder.addConstraintMatch(ruleContext2, -2.00); // Rule match added
        callUnMatch(ruleContext2); // Rule match removed

        RuleContext ruleContext3 = createRuleContext("scoreRule3");
        scoreHolder.addConstraintMatch(ruleContext3, -0.30); // Rule match added
        scoreHolder.addConstraintMatch(ruleContext3, -0.03); // Rule match modified

        assertEquals(SimpleDoubleScore.valueOf(-10.03), scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(3, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
