/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class HardSoftBigDecimalScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardSoftBigDecimalScoreHolder scoreHolder = new HardSoftBigDecimalScoreHolder(constraintMatchEnabled);

        scoreHolder.addHardConstraintMatch(createRuleContext("scoreRule1"),
                new BigDecimal("-10.00")); // Rule match added

        RuleContext ruleContext2 = createRuleContext("scoreRule2");
        scoreHolder.addHardConstraintMatch(ruleContext2, new BigDecimal("-2.00")); // Rule match added
        callUnMatch(ruleContext2); // Rule match removed

        RuleContext ruleContext3 = createRuleContext("scoreRule3");
        scoreHolder.addSoftConstraintMatch(ruleContext3, new BigDecimal("-0.30")); // Rule match added
        scoreHolder.addSoftConstraintMatch(ruleContext3, new BigDecimal("-0.03")); // Rule match modified
        scoreHolder.addHardConstraintMatch(ruleContext3, new BigDecimal("-3.00")); // Rule of different level added
        scoreHolder.addHardConstraintMatch(ruleContext3, new BigDecimal("-4.00")); // Rule of different level modified

        RuleContext ruleContext4 = createRuleContext("scoreRule4");
        scoreHolder.addHardConstraintMatch(ruleContext4, new BigDecimal("-1.00"));
        scoreHolder.addSoftConstraintMatch(ruleContext4, new BigDecimal("-1.00"));
        callUnMatch(ruleContext4, 1); // Rule match removed - 1st score level (soft)

        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("-15.00"), new BigDecimal("-0.03")),
                scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(6, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
