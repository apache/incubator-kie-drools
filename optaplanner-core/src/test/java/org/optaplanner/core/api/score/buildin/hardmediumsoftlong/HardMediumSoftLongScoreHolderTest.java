/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.api.score.buildin.hardmediumsoftlong;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.junit.Assert.*;

public class HardMediumSoftLongScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        HardMediumSoftLongScoreHolder scoreHolder = new HardMediumSoftLongScoreHolder(constraintMatchEnabled);

        scoreHolder.addHardConstraintMatch(createRuleContext("scoreRule1"), -1000L); // Rule match added

        RuleContext ruleContext2 = createRuleContext("scoreRule2");
        scoreHolder.addHardConstraintMatch(ruleContext2, -200L); // Rule match added
        callUnMatch(ruleContext2); // Rule match removed

        RuleContext ruleContext3 = createRuleContext("scoreRule3");
        scoreHolder.addMediumConstraintMatch(ruleContext3, -30L); // Rule match added
        scoreHolder.addMediumConstraintMatch(ruleContext3, -3L); // Rule match modified
        scoreHolder.addHardConstraintMatch(ruleContext3, -300L); // Rule of different level added
        scoreHolder.addHardConstraintMatch(ruleContext3, -400L); // Rule of different level modified

        scoreHolder.addSoftConstraintMatch(createRuleContext("scoreRule4"), -4L); // Rule match added

        RuleContext ruleContext5 = createRuleContext("scoreRule5");
        scoreHolder.addHardConstraintMatch(ruleContext5, -1L);
        scoreHolder.addSoftConstraintMatch(ruleContext5, -1L);
        callUnMatch(ruleContext5, 2); // Rule match removed - 2nd score level (soft)

        assertEquals(HardMediumSoftLongScore.valueOf(-1401L, -3L, -4L), scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(7, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
