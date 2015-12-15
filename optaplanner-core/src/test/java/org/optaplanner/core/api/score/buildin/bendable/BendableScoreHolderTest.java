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

        scoreHolder.addHardConstraintMatch(mockRuleContext("scoreRule1"), 0, -10000);
        assertEquals(BendableScore.valueOf(new int[]{-10000}, new int[]{0, 0}), scoreHolder.extractScore());

        RuleContext ruleContext2 = mockRuleContext("scoreRule2");
        scoreHolder.addHardConstraintMatch(ruleContext2, 0, -2000);
        assertEquals(BendableScore.valueOf(new int[]{-12000}, new int[]{0, 0}), scoreHolder.extractScore());
        callUnMatch(ruleContext2);
        assertEquals(BendableScore.valueOf(new int[]{-10000}, new int[]{0, 0}), scoreHolder.extractScore());

        RuleContext ruleContext3 = mockRuleContext("scoreRule3");
        scoreHolder.addHardConstraintMatch(ruleContext3, 0, -900);
        scoreHolder.addHardConstraintMatch(ruleContext3, 0, -300); // Overwrite existing
        scoreHolder.addSoftConstraintMatch(ruleContext3, 0, -90); // Different score level
        scoreHolder.addSoftConstraintMatch(ruleContext3, 0, -40); // Overwrite existing
        assertEquals(BendableScore.valueOf(new int[]{-10300}, new int[]{-40, 0}), scoreHolder.extractScore());

        scoreHolder.addSoftConstraintMatch(mockRuleContext("scoreRule4"), 1, -5);
        assertEquals(BendableScore.valueOf(new int[]{-10300}, new int[]{-40, -5}), scoreHolder.extractScore());

        RuleContext ruleContext5 = mockRuleContext("scoreRule5");
        scoreHolder.addHardConstraintMatch(ruleContext5, 0, -2000);
        scoreHolder.addSoftConstraintMatch(ruleContext5, 0, -2000); // Different score level
        assertEquals(BendableScore.valueOf(new int[]{-12300}, new int[]{-2040, -5}), scoreHolder.extractScore());
        callUnMatch(ruleContext5);
        assertEquals(BendableScore.valueOf(new int[]{-10300}, new int[]{-40, -5}), scoreHolder.extractScore());

        if (constraintMatchEnabled) {
            assertEquals(7, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
