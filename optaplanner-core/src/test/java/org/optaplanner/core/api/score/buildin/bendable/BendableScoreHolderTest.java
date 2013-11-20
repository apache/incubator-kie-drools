package org.optaplanner.core.api.score.buildin.bendable;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class BendableScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        BendableScoreHolder scoreHolder = new BendableScoreHolder(constraintMatchEnabled, 1, 2);

        scoreHolder.addHardConstraintMatch(createRuleContext("scoreRule1"), 0, -1000); // Rule match added

        RuleContext ruleContext2 = createRuleContext("scoreRule2");
        scoreHolder.addHardConstraintMatch(ruleContext2, 0, -200); // Rule match added
        callUnMatch(ruleContext2); // Rule match removed

        RuleContext ruleContext3 = createRuleContext("scoreRule3");
        scoreHolder.addSoftConstraintMatch(ruleContext3, 0, -30); // Rule match added
        scoreHolder.addSoftConstraintMatch(ruleContext3, 0, -3); // Rule match modified

        scoreHolder.addSoftConstraintMatch(createRuleContext("scoreRule4"), 1, -4); // Rule match added

        assertEquals(BendableScore.valueOf(new int[]{-1000}, new int[]{-3, -4}), scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(4, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
