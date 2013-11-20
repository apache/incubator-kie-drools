package org.optaplanner.core.api.score.buildin.hardsoft;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class HardSoftScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardSoftScoreHolder scoreHolder = new HardSoftScoreHolder(constraintMatchEnabled);

        scoreHolder.addHardConstraintMatch(createRuleContext("scoreRule1"), -1000); // Rule match added

        RuleContext ruleContext2 = createRuleContext("scoreRule2");
        scoreHolder.addHardConstraintMatch(ruleContext2, -200); // Rule match added
        callUnMatch(ruleContext2); // Rule match removed

        RuleContext ruleContext3 = createRuleContext("scoreRule3");
        scoreHolder.addSoftConstraintMatch(ruleContext3, -30); // Rule match added
        scoreHolder.addSoftConstraintMatch(ruleContext3, -3); // Rule match modified

        assertEquals(HardSoftScore.valueOf(-1000, -3), scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(3, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
