/*
 * Copyright 2013 JBoss Inc
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

package org.drools.planner.core.score.buildin.hardmediumsoft;

import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.buildin.AbstractScoreTest;
import org.drools.planner.core.score.buildin.hardmediumsoft.DefaultHardMediumSoftScore;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultHardMediumSoftScoreTest extends AbstractScoreTest {

    @Test
    public void compareTo() {
        assertScoreOrder(
                DefaultHardMediumSoftScore.valueOf(-20, Integer.MIN_VALUE, Integer.MIN_VALUE),
                DefaultHardMediumSoftScore.valueOf(-20, Integer.MIN_VALUE, -20),
                DefaultHardMediumSoftScore.valueOf(-20, Integer.MIN_VALUE, 1),
                DefaultHardMediumSoftScore.valueOf(-20, -300, -4000),
                DefaultHardMediumSoftScore.valueOf(-20, -300, -300),
                DefaultHardMediumSoftScore.valueOf(-20, -300, -20),
                DefaultHardMediumSoftScore.valueOf(-20, -300, 300),
                DefaultHardMediumSoftScore.valueOf(-20, -20, -300),
                DefaultHardMediumSoftScore.valueOf(-20, -20, 0),
                DefaultHardMediumSoftScore.valueOf(-20, -20, 1),
                DefaultHardMediumSoftScore.valueOf(-1, -300, -4000),
                DefaultHardMediumSoftScore.valueOf(-1, -300, -20),
                DefaultHardMediumSoftScore.valueOf(-1, -20, -300),
                DefaultHardMediumSoftScore.valueOf(1, Integer.MIN_VALUE, -20),
                DefaultHardMediumSoftScore.valueOf(1, -20, Integer.MIN_VALUE)
        );
    }

    @Test
    public void feasible() {
        assertEquals(true, DefaultHardMediumSoftScore.valueOf(0, -300, -4000).isFeasible());
        assertEquals(false, DefaultHardMediumSoftScore.valueOf(-5, -300, -4000).isFeasible());
        assertEquals(true, DefaultHardMediumSoftScore.valueOf(2, -300, -4000).isFeasible());
    }

}
