/*
 * Copyright 2014 JBoss Inc
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
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendable.BendableScoreHolder;
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

        long value1 = 1000000000000L;
        long value2 = 1000000000001L;
        long value3 = 1000000000002L;
        long value4 = 1000000000003L;
        long value5 = -1000000000004L;

        scoreHolder.addHardConstraintMatch(createRuleContext("scoreRule1"), 0,  value1); // Rule match added

        RuleContext ruleContext2 = createRuleContext("scoreRule2");
        scoreHolder.addHardConstraintMatch(ruleContext2, 0, value2); // Rule match added
        callUnMatch(ruleContext2); // Rule match removed

        RuleContext ruleContext3 = createRuleContext("scoreRule3");
        scoreHolder.addSoftConstraintMatch(ruleContext3, 0, value3); // Rule match added
        scoreHolder.addSoftConstraintMatch(ruleContext3, 0, value4); // Rule match modified

        scoreHolder.addSoftConstraintMatch(createRuleContext("scoreRule4"), 1, value5); // Rule match added

        assertEquals(BendableLongScore.valueOf(new long[]{value1},
                new long[]{value4, value5}), scoreHolder.extractScore());
        if (constraintMatchEnabled) {
            assertEquals(4, scoreHolder.getConstraintMatchTotals().size());
        }
    }

}
