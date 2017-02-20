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

package org.optaplanner.core.api.score.buildin.hardsoftlong;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class HardSoftLongScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardSoftLongScoreHolder scoreHolder = new HardSoftLongScoreHolder(constraintMatchEnabled);

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, -1L);
        assertEquals(HardSoftLongScore.valueOf(-1L, -0L), scoreHolder.extractScore(0));

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, -8L);
        assertEquals(HardSoftLongScore.valueOf(-9L, -0L), scoreHolder.extractScore(0));
        callUnMatch(hard2Undo);
        assertEquals(HardSoftLongScore.valueOf(-1L, -0L), scoreHolder.extractScore(0));

        RuleContext soft1 = mockRuleContext("soft1");
        scoreHolder.addSoftConstraintMatch(soft1, -10L);
        scoreHolder.addSoftConstraintMatch(soft1, -20L); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, -100L, -1000L);
        scoreHolder.addMultiConstraintMatch(multi1, -300L, -4000L); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, -10000L);
        scoreHolder.addHardConstraintMatch(hard3, -50000L); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo");
        scoreHolder.addSoftConstraintMatch(soft2Undo, -99L);
        callUnMatch(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, -999L, -999L);
        callUnMatch(multi2Undo);

        assertEquals(HardSoftLongScore.valueOf(-50301L, -4020L), scoreHolder.extractScore(0));
        assertEquals(HardSoftLongScore.valueOfUninitialized(-7, -50301L, -4020L), scoreHolder.extractScore(-7));
        if (constraintMatchEnabled) {
            assertEquals(7, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
