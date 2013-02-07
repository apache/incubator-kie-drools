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

package org.drools.planner.core.score.buildin.simplebigdecimal;

import java.math.BigDecimal;

import org.drools.planner.core.score.buildin.AbstractScoreTest;
import org.junit.Test;

public class SimpleBigDecimalScoreTest extends AbstractScoreTest {

    @Test
    public void compareTo() {
        assertScoreOrder(
                SimpleBigDecimalScore.valueOf(new BigDecimal("-300.5")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("-300")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("-20.067")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("-20.007")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("-20")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("-1")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("0")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("1"))
        );
    }

}
