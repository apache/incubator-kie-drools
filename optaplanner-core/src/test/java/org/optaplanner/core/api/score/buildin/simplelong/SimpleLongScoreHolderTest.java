package org.optaplanner.core.api.score.buildin.simplelong;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class SimpleLongScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        SimpleLongScoreHolder scoreHolder = new SimpleLongScoreHolder(constraintMatchEnabled);

        scoreHolder.addConstraintMatch(createRuleContext("scoreRule1"), -1000L); // Rule match added

        RuleContext ruleContext2 = createRuleContext("scoreRule2");
        scoreHolder.addConstraintMatch(ruleContext2, -200L); // Rule match added
        callUnMatch(ruleContext2); // Rule match removed

        RuleContext ruleContext3 = createRuleContext("scoreRule3");
        scoreHolder.addConstraintMatch(ruleContext3, -30L); // Rule match added
        scoreHolder.addConstraintMatch(ruleContext3, -3L); // Rule match modified

        assertEquals(SimpleLongScore.valueOf(-1003L), scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(3, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
