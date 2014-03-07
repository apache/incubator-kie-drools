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

package org.optaplanner.core.impl.score.buildin.simplebigdecimal;

import java.math.BigDecimal;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;

import static org.junit.Assert.assertEquals;

public class SimpleBigDecimalScoreDefinitionTest {

    @Test
    public void testCalculateTimeGradient() {
        SimpleBigDecimalScoreDefinition scoreDefinition = new SimpleBigDecimalScoreDefinition();

        assertEquals(0.0, scoreDefinition.calculateTimeGradient(
                SimpleBigDecimalScore.valueOf(new BigDecimal("0.00")), SimpleBigDecimalScore.valueOf(new BigDecimal("10.00")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("0.00"))), 0.0);
        assertEquals(0.6, scoreDefinition.calculateTimeGradient(
                SimpleBigDecimalScore.valueOf(new BigDecimal("0.00")), SimpleBigDecimalScore.valueOf(new BigDecimal("10.00")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("6.00"))), 0.0);
        assertEquals(1.0, scoreDefinition.calculateTimeGradient(
                SimpleBigDecimalScore.valueOf(new BigDecimal("0.00")), SimpleBigDecimalScore.valueOf(new BigDecimal("10.00")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("10.00"))), 0.0);
        assertEquals(1.0, scoreDefinition.calculateTimeGradient(
                SimpleBigDecimalScore.valueOf(new BigDecimal("0.00")), SimpleBigDecimalScore.valueOf(new BigDecimal("10.00")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("11.00"))), 0.0);
        assertEquals(0.25, scoreDefinition.calculateTimeGradient(
                SimpleBigDecimalScore.valueOf(new BigDecimal("-10.00")), SimpleBigDecimalScore.valueOf(new BigDecimal("30.00")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("0.00"))), 0.0);
        assertEquals(0.33333, scoreDefinition.calculateTimeGradient(
                SimpleBigDecimalScore.valueOf(new BigDecimal("10.00")), SimpleBigDecimalScore.valueOf(new BigDecimal("40.00")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("20.00"))), 0.00001);
    }

}
