package org.optaplanner.core.api.score.buildin.hardmediumsoft;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class HardMediumSoftScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardMediumSoftScoreHolder scoreHolder = new HardMediumSoftScoreHolder(constraintMatchEnabled);

        scoreHolder.addHardConstraintMatch(createRuleContext("scoreRule1"), -1000); // Rule match added

        RuleContext ruleContext2 = createRuleContext("scoreRule2");
        scoreHolder.addHardConstraintMatch(ruleContext2, -200); // Rule match added
        callUnMatch(ruleContext2); // Rule match removed

        RuleContext ruleContext3 = createRuleContext("scoreRule3");
        scoreHolder.addMediumConstraintMatch(ruleContext3, -30); // Rule match added
        scoreHolder.addMediumConstraintMatch(ruleContext3, -3); // Rule match modified

        scoreHolder.addSoftConstraintMatch(createRuleContext("scoreRule4"), -4); // Rule match added

        assertEquals(HardMediumSoftScore.valueOf(-1000, -3, -4), scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(4, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
