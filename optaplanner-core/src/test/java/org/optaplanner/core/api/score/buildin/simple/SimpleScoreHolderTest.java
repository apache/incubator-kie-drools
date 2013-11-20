package org.optaplanner.core.api.score.buildin.simple;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class SimpleScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        SimpleScoreHolder scoreHolder = new SimpleScoreHolder(constraintMatchEnabled);

        scoreHolder.addConstraintMatch(createRuleContext("scoreRule1"), -1000); // Rule match added

        RuleContext ruleContext2 = createRuleContext("scoreRule2");
        scoreHolder.addConstraintMatch(ruleContext2, -200); // Rule match added
        callUnMatch(ruleContext2); // Rule match removed

        RuleContext ruleContext3 = createRuleContext("scoreRule3");
        scoreHolder.addConstraintMatch(ruleContext3, -30); // Rule match added
        scoreHolder.addConstraintMatch(ruleContext3, -3); // Rule match modified

        assertEquals(SimpleScore.valueOf(-1003), scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(3, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
