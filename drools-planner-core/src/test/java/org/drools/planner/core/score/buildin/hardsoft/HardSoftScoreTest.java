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

import org.drools.planner.core.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class HardSoftScoreTest extends AbstractScoreTest {

    @Test
    public void compareTo() {
        assertScoreOrder(
                HardSoftScore.valueOf(-20, Integer.MIN_VALUE),
                HardSoftScore.valueOf(-20, -20),
                HardSoftScore.valueOf(-1, -300),
                HardSoftScore.valueOf(-1, 4000),
                HardSoftScore.valueOf(0, -1),
                HardSoftScore.valueOf(0, 0),
                HardSoftScore.valueOf(0, 1)
        );
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftScore.valueOf(-5, -300)
        );
        assertScoreFeasible(
                HardSoftScore.valueOf(0, -300),
                HardSoftScore.valueOf(2, -300)
        );
    }

}
