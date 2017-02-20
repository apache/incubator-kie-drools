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

        RuleContext hard1 = mockRuleContext("hard1");
        scoreHolder.addHardConstraintMatch(hard1, -1);

        RuleContext hard2Undo = mockRuleContext("hard2Undo");
        scoreHolder.addHardConstraintMatch(hard2Undo, -9);
        callUnMatch(hard2Undo);

        RuleContext soft1 = mockRuleContext("soft1");
        scoreHolder.addSoftConstraintMatch(soft1, -10);
        scoreHolder.addSoftConstraintMatch(soft1, -20); // Overwrite existing

        RuleContext multi1 = mockRuleContext("multi1");
        scoreHolder.addMultiConstraintMatch(multi1, -100, -1000);
        scoreHolder.addMultiConstraintMatch(multi1, -300, -4000); // Overwrite existing

        RuleContext hard3 = mockRuleContext("hard3");
        scoreHolder.addHardConstraintMatch(hard3, -10000);
        scoreHolder.addHardConstraintMatch(hard3, -50000); // Overwrite existing

        RuleContext soft2Undo = mockRuleContext("soft2Undo");
        scoreHolder.addSoftConstraintMatch(soft2Undo, -99);
        callUnMatch(soft2Undo);

        RuleContext multi2Undo = mockRuleContext("multi2Undo");
        scoreHolder.addMultiConstraintMatch(multi2Undo, -999, -999);
        callUnMatch(multi2Undo);

        assertEquals(HardSoftScore.valueOf(-50301, -4020), scoreHolder.extractScore(0));
        assertEquals(HardSoftScore.valueOfUninitialized(-7, -50301, -4020), scoreHolder.extractScore(-7));
        if (constraintMatchEnabled) {
            assertEquals(7, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
