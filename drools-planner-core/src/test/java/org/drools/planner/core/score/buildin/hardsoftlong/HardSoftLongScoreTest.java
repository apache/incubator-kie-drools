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

package org.drools.planner.core.score.buildin.hardsoftlong;

import org.drools.planner.core.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class HardSoftLongScoreTest extends AbstractScoreTest {

    @Test
    public void compareTo() {
        assertScoreOrder(
                HardSoftLongScore.valueOf(-20L, Long.MIN_VALUE),
                HardSoftLongScore.valueOf(-20L, -20L),
                HardSoftLongScore.valueOf(-1L, -300L),
                HardSoftLongScore.valueOf(-1L, 4000L),
                HardSoftLongScore.valueOf(0L, -1L),
                HardSoftLongScore.valueOf(0L, 0L),
                HardSoftLongScore.valueOf(0L, 1L)
        );
    }

    @Test
    public void feasible() {
        assertEquals(true, HardSoftLongScore.valueOf(0L, -300L).isFeasible());
        assertEquals(false, HardSoftLongScore.valueOf(-5L, -300L).isFeasible());
        assertEquals(true, HardSoftLongScore.valueOf(2L, -300L).isFeasible());
    }

}
