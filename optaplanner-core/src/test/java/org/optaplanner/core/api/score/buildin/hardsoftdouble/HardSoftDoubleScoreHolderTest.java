package org.optaplanner.core.api.score.buildin.hardsoftdouble;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class HardSoftDoubleScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardSoftDoubleScoreHolder scoreHolder = new HardSoftDoubleScoreHolder(constraintMatchEnabled);

        scoreHolder.addHardConstraintMatch(createRuleContext("scoreRule1"), -10.00); // Rule match added

        RuleContext ruleContext2 = createRuleContext("scoreRule2");
        scoreHolder.addHardConstraintMatch(ruleContext2, -2.00); // Rule match added
        callUnMatch(ruleContext2); // Rule match removed

        RuleContext ruleContext3 = createRuleContext("scoreRule3");
        scoreHolder.addSoftConstraintMatch(ruleContext3, -0.30); // Rule match added
        scoreHolder.addSoftConstraintMatch(ruleContext3, -0.06); // Rule match modified

        assertEquals(HardSoftDoubleScore.valueOf(-10.00, -0.06), scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(3, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
