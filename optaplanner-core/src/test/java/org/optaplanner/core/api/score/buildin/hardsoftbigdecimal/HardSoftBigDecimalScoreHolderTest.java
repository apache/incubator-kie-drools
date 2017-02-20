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

package org.optaplanner.core.api.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftdouble.HardSoftDoubleScore;
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

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, new BigDecimal("-0.01"));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, new BigDecimal("-0.09"));
        callUnMatch(hard2Undo);

        RuleContext soft1 = mockRuleContext("soft1");
        scoreHolder.addSoftConstraintMatch(soft1, new BigDecimal("-0.10"));
        scoreHolder.addSoftConstraintMatch(soft1, new BigDecimal("-0.20")); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, new BigDecimal("-1.00"), new BigDecimal("-10.00"));
        scoreHolder.addMultiConstraintMatch(multi1, new BigDecimal("-3.00"), new BigDecimal("-40.00")); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, new BigDecimal("-100.00"));
        scoreHolder.addHardConstraintMatch(hard3, new BigDecimal("-500.00")); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo");
        scoreHolder.addSoftConstraintMatch(soft2Undo, new BigDecimal("-0.99"));
        callUnMatch(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, new BigDecimal("-9.99"), new BigDecimal("-9.99"));
        callUnMatch(multi2Undo);

        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("-503.01"), new BigDecimal("-40.20")), scoreHolder.extractScore(0));
        assertEquals(HardSoftBigDecimalScore.valueOfUninitialized(-7, new BigDecimal("-503.01"), new BigDecimal("-40.20")), scoreHolder.extractScore(-7));
        if (constraintMatchEnabled) {
            assertEquals(7, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
