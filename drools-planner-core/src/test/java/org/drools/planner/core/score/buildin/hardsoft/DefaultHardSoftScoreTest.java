/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.core.score.buildin.hardsoft;

import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultHardSoftScoreTest extends AbstractScoreTest {

    @Test
    public void compareTo() {
        assertScoreOrder(
                DefaultHardSoftScore.valueOf(-20, Integer.MIN_VALUE),
                DefaultHardSoftScore.valueOf(-20, -20),
                DefaultHardSoftScore.valueOf(-1, -300),
                DefaultHardSoftScore.valueOf(-1, 4000),
                DefaultHardSoftScore.valueOf(0, -1),
                DefaultHardSoftScore.valueOf(0, 0),
                DefaultHardSoftScore.valueOf(0, 1)
        );
    }

    @Test
    public void feasible() {
        assertEquals(true, DefaultHardSoftScore.valueOf(0, -300).isFeasible());
        assertEquals(false, DefaultHardSoftScore.valueOf(-5, -300).isFeasible());
        assertEquals(true, DefaultHardSoftScore.valueOf(2, -300).isFeasible());
    }

}
