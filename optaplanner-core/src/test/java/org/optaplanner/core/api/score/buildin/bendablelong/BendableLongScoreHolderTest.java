/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.score.buildin.bendablelong;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.assertEquals;

public class BendableLongScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        BendableLongScoreHolder scoreHolder = new BendableLongScoreHolder(constraintMatchEnabled, 1, 2);

        scoreHolder.addHardConstraintMatch(mockRuleContext("scoreRule1"), 0, 1000000000001L);

        RuleContext ruleContext2 = mockRuleContext("scoreRule2");
        scoreHolder.addHardConstraintMatch(ruleContext2, 0, 1000000000020L);
        callUnMatch(ruleContext2);

        RuleContext ruleContext3 = mockRuleContext("scoreRule3");
        scoreHolder.addSoftConstraintMatch(ruleContext3, 0, 1000000000300L);
        scoreHolder.addSoftConstraintMatch(ruleContext3, 0, 1000000040000L); // Overwrite existing
        scoreHolder.addHardConstraintMatch(ruleContext3, 0, 1000000000300L); // Different score level
        scoreHolder.addHardConstraintMatch(ruleContext3, 0, 1000000000400L); // Overwrite existing

        scoreHolder.addSoftConstraintMatch(mockRuleContext("scoreRule4"), 1, -1000000500000L);

        RuleContext ruleContext5 = mockRuleContext("scoreRule5");
        scoreHolder.addHardConstraintMatch(ruleContext5, 0, 1000000000001L);
        scoreHolder.addSoftConstraintMatch(ruleContext5, 0, 1000000000001L); // Different score level
        callUnMatch(ruleContext5);

        assertEquals(BendableLongScore.valueOf(new long[]{2000000000401L},
                new long[]{1000000040000L, -1000000500000L}), scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(7, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
