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

package org.drools.planner.core.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;

import org.drools.planner.core.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class HardSoftBigDecimalScoreTest extends AbstractScoreTest {

    @Test
    public void compareTo() {
        assertScoreOrder(
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-20.06"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-20.007"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-20"), new BigDecimal("-20.06")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-20"), new BigDecimal("-20.007")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-20"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-1"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-1"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("0"), new BigDecimal("-1")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("0"), new BigDecimal("0")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("0"), new BigDecimal("1"))
        );
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-5"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-5"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-0.007"), new BigDecimal("4000"))
        );
        assertScoreFeasible(
                HardSoftBigDecimalScore.valueOf(new BigDecimal("0"), new BigDecimal("-300.007")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("0"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("2"), new BigDecimal("-300"))
        );
    }

}
