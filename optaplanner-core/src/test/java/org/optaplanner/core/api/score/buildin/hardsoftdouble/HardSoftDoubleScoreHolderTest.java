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

package org.optaplanner.core.api.score.buildin.hardsoftdouble;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class HardSoftDoubleScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardSoftDoubleScoreHolder scoreHolder = new HardSoftDoubleScoreHolder(constraintMatchEnabled);

        scoreHolder.addHardConstraintMatch(mockRuleContext("scoreRule1"), -10.00);

        RuleContext ruleContext2 = mockRuleContext("scoreRule2");
        scoreHolder.addHardConstraintMatch(ruleContext2, -2.00);
        callUnMatch(ruleContext2);

        RuleContext ruleContext3 = mockRuleContext("scoreRule3");
        scoreHolder.addSoftConstraintMatch(ruleContext3, -0.30);
        scoreHolder.addSoftConstraintMatch(ruleContext3, -0.06); // Overwrite existing
        scoreHolder.addHardConstraintMatch(ruleContext3, -3.00); // Different score level
        scoreHolder.addHardConstraintMatch(ruleContext3, -4.00); // Overwrite existing

        RuleContext ruleContext4 = mockRuleContext("scoreRule4");
        scoreHolder.addHardConstraintMatch(ruleContext4, -1.00);
        scoreHolder.addSoftConstraintMatch(ruleContext4, -0.02);
        callUnMatch(ruleContext4);

        assertEquals(HardSoftDoubleScore.valueOf(-14.00, -0.06), scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(6, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
