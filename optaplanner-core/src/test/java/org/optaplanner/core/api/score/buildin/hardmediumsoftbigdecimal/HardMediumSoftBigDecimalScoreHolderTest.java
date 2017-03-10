/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal;

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class HardMediumSoftBigDecimalScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardMediumSoftBigDecimalScoreHolder scoreHolder = new HardMediumSoftBigDecimalScoreHolder(constraintMatchEnabled);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, new BigDecimal("-0.01"));
        assertEquals(HardMediumSoftBigDecimalScore.valueOf(new BigDecimal("-0.01"), new BigDecimal("0.00"), new BigDecimal("0.00")), scoreHolder.extractScore(0));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, new BigDecimal("-0.08"));
        assertEquals(HardMediumSoftBigDecimalScore.valueOf(new BigDecimal("-0.09"), new BigDecimal("0.00"), new BigDecimal("0.00")), scoreHolder.extractScore(0));
        callUnMatch(hard2Undo);
        assertEquals(HardMediumSoftBigDecimalScore.valueOf(new BigDecimal("-0.01"), new BigDecimal("0.00"), new BigDecimal("0.00")), scoreHolder.extractScore(0));

        RuleContext medium1 = mockRuleContext("medium1");
        scoreHolder.addMediumConstraintMatch(medium1, new BigDecimal("-0.10"));
        scoreHolder.addMediumConstraintMatch(medium1, new BigDecimal("-0.20")); // Overwrite existing

        RuleContext soft1 = mockRuleContext("soft1", DEFAULT_JUSTIFICATION, OTHER_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft1, new BigDecimal("-1.00"));
        scoreHolder.addSoftConstraintMatch(soft1, new BigDecimal("-3.00")); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, new BigDecimal("-10.00"), new BigDecimal("-100.00"), new BigDecimal("-1000.00"));
        scoreHolder.addMultiConstraintMatch(multi1, new BigDecimal("-40.00"), new BigDecimal("-500.00"), new BigDecimal("-6000.00")); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, new BigDecimal("-10000.00"));
        scoreHolder.addHardConstraintMatch(hard3, new BigDecimal("-70000.00")); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo", UNDO_JUSTIFICATION);
        scoreHolder.addSoftConstraintMatch(soft2Undo, new BigDecimal("-0.99"));
        callUnMatch(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, new BigDecimal("-9.99"), new BigDecimal("-9.99"), new BigDecimal("-9.99"));
        callUnMatch(multi2Undo);

        RuleContext medium2Undo = mockRuleContext("medium2Undo");
        scoreHolder.addMediumConstraintMatch(medium2Undo, new BigDecimal("-99.99"));
        callUnMatch(medium2Undo);

        assertEquals(HardMediumSoftBigDecimalScore.valueOf(new BigDecimal("-70040.01"), new BigDecimal("-500.20"), new BigDecimal("-6003.00")), scoreHolder.extractScore(0));
        assertEquals(HardMediumSoftBigDecimalScore.valueOfUninitialized(-7, new BigDecimal("-70040.01"), new BigDecimal("-500.20"), new BigDecimal("-6003.00")), scoreHolder.extractScore(-7));
        if (constraintMatchEnabled) {
            assertEquals(HardMediumSoftBigDecimalScore.valueOf(new BigDecimal("-0.01"), new BigDecimal("0.00"), new BigDecimal("0.00")), findConstraintMatchTotal(scoreHolder, "hard1").getScoreTotal());
            assertEquals(HardMediumSoftBigDecimalScore.valueOf(new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("-3.00")), scoreHolder.getIndictmentMap().get(OTHER_JUSTIFICATION).getScoreTotal());
            assertNull(scoreHolder.getIndictmentMap().get(UNDO_JUSTIFICATION));
        }
    }

}
