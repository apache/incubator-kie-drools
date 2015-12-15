/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class BendableBigDecimalScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        BendableBigDecimalScoreHolder scoreHolder = new BendableBigDecimalScoreHolder(constraintMatchEnabled, 1, 2);

        scoreHolder.addHardConstraintMatch(mockRuleContext("scoreRule1"), 0, BigDecimal.valueOf(-10000));

        RuleContext ruleContext2 = mockRuleContext("scoreRule2");
        scoreHolder.addHardConstraintMatch(ruleContext2, 0, BigDecimal.valueOf(-2000));
        callUnMatch(ruleContext2);

        RuleContext ruleContext3 = mockRuleContext("scoreRule3");
        scoreHolder.addSoftConstraintMatch(ruleContext3, 0, BigDecimal.valueOf(-90));
        scoreHolder.addSoftConstraintMatch(ruleContext3, 0, BigDecimal.valueOf(-40)); // Overwrite existing
        scoreHolder.addHardConstraintMatch(ruleContext3, 0, BigDecimal.valueOf(-900)); // Different score level
        scoreHolder.addHardConstraintMatch(ruleContext3, 0, BigDecimal.valueOf(-300)); // Overwrite existing

        scoreHolder.addSoftConstraintMatch(mockRuleContext("scoreRule4"), 1, BigDecimal.valueOf(-5));

        RuleContext ruleContext5 = mockRuleContext("scoreRule5");
        scoreHolder.addHardConstraintMatch(ruleContext5, 0, BigDecimal.valueOf(-900));
        scoreHolder.addSoftConstraintMatch(ruleContext5, 0, BigDecimal.valueOf(-9)); // Different score level
        callUnMatch(ruleContext5);
        assertEquals(BendableBigDecimalScore.valueOf(new BigDecimal[]{BigDecimal.valueOf(-10300)},
                new BigDecimal[]{BigDecimal.valueOf(-40), BigDecimal.valueOf(-5)}), scoreHolder.extractScore());

        if (constraintMatchEnabled) {
            assertEquals(7, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
