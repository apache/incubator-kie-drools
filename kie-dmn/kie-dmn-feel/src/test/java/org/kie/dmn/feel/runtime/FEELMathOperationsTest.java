/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.runtime;

import java.math.BigDecimal;
import java.util.Collection;

import org.junit.runners.Parameterized;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.util.EvalHelper;

public class FEELMathOperationsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{3}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                { "10+5", BigDecimal.valueOf( 15 ) , null},
                { "-10 + -5", BigDecimal.valueOf( -15 ) , null},
                { "(-10) + (-5)", BigDecimal.valueOf( -15 ) , null},
                { "10-5", BigDecimal.valueOf( 5 ) , null},
                { "-10 - -5", BigDecimal.valueOf( -5 ) , null},
                { "(-10) - (-5)", BigDecimal.valueOf( -5 ) , null},
                { "(10 + 20) - (-5 + 3)", BigDecimal.valueOf( 32 ) , null},
                { "10*5", BigDecimal.valueOf( 50 ) , null},
                { "-10 * -5", BigDecimal.valueOf( 50 ) , null},
                { "(-10) * (-5)", BigDecimal.valueOf( 50 ) , null},
                { "(10 + 20) * (-5 * 3)", BigDecimal.valueOf( -450 ) , null},
                { "10/5", BigDecimal.valueOf( 2 ) , null},
                { "-10 / -5", BigDecimal.valueOf( 2 ) , null},
                { "(-10) / (-5)", BigDecimal.valueOf( 2 ) , null},
                { "(10 + 20) / (-5 * 3)", BigDecimal.valueOf( -2 ) , null},
                { "(10 + 20) / 0", null , null},
                { "10 ** 5", BigDecimal.valueOf( 100000 ) , null},
                { "10 ** -5", new BigDecimal( "0.00001" ) , null},
                { "(5+2) ** 5", BigDecimal.valueOf( 16807 ) , null},
                { "5+2 ** 5", BigDecimal.valueOf( 37 ) , null},
                { "5+2 ** 5+3", BigDecimal.valueOf( 40 ) , null},
                { "5+2 ** (5+3)", BigDecimal.valueOf( 261 ) , null},
                {"2 ** 3.5", new BigDecimal("11.31370849898476039041350979367758"), null},
                { "10 + null", null , null},
                { "null + 10", null , null},
                { "10 - null", null , null},
                { "null - 10", null , null},
                { "10 * null", null , null},
                { "null * 10", null , null},
                { "10 / null", null , null},
                { "null / 10", null , null},
                { "10 + 20 / -5 - 3", BigDecimal.valueOf( 3 ) , null},
                { "10 + 20 / ( -5 - 3 )", BigDecimal.valueOf( 7.5 ) , null},
                { "1.2*10**3", BigDecimal.valueOf( 1200.0 ) , null},
                { "1 ++++++ 2", null, FEELEvent.Severity.ERROR},
                { "1 -- 2", BigDecimal.valueOf(3), null},
                { "null + null", null, null},
                { "-1", BigDecimal.valueOf( -1 ), null },
                { "--1", BigDecimal.valueOf( 1 ), null },
                { "---1", BigDecimal.valueOf( -1 ), null },
                { "{ amount : 100000.00, rate : 0.25, term : 36, PMT : (amount *rate/12) / (1 - (1 + rate/12)**-term) }.PMT", EvalHelper.getBigDecimalOrNull( "3975.982590125552338278440100112431" ), null},
        };
        return addAdditionalParameters(cases, false);
    }
}
