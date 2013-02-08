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

package org.drools.planner.core.score.buildin.hardsoftdouble;

import org.drools.planner.core.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class HardSoftDoubleScoreTest extends AbstractScoreTest {

    @Test
    public void compareTo() {
        assertScoreOrder(
                HardSoftDoubleScore.valueOf(-20.06, -20.0),
                HardSoftDoubleScore.valueOf(-20.007, -20.0),
                HardSoftDoubleScore.valueOf(-20.0, -Double.MAX_VALUE),
                HardSoftDoubleScore.valueOf(-20.0, -20.06),
                HardSoftDoubleScore.valueOf(-20.0, -20.007),
                HardSoftDoubleScore.valueOf(-20.0, -20.0),
                HardSoftDoubleScore.valueOf(-1.0, -300.0),
                HardSoftDoubleScore.valueOf(-1.0, 4000.0),
                HardSoftDoubleScore.valueOf(0.0, -1.0),
                HardSoftDoubleScore.valueOf(0.0, 0.0),
                HardSoftDoubleScore.valueOf(0.0, 1.0)
        );
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftDoubleScore.valueOf(-5.0, -300.0),
                HardSoftDoubleScore.valueOf(-5.0, 4000.0),
                HardSoftDoubleScore.valueOf(-0.007, 4000.0)
        );
        assertScoreFeasible(
                HardSoftDoubleScore.valueOf(0.0, -300.007),
                HardSoftDoubleScore.valueOf(0.0, -300.0),
                HardSoftDoubleScore.valueOf(2.0, -300.0)
        );
    }

}
