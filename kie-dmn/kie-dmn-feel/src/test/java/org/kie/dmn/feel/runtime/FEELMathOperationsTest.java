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
import java.util.Arrays;
import java.util.Collection;
import org.junit.runners.Parameterized;

public class FEELMathOperationsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                { "10+5", BigDecimal.valueOf( 15 ) },
                { "-10 + -5", BigDecimal.valueOf( -15 ) },
                { "(-10) + (-5)", BigDecimal.valueOf( -15 ) },
                { "10-5", BigDecimal.valueOf( 5 ) },
                { "-10 - -5", BigDecimal.valueOf( -5 ) },
                { "(-10) - (-5)", BigDecimal.valueOf( -5 ) },
                { "(10 + 20) - (-5 + 3)", BigDecimal.valueOf( 32 ) },
                { "10*5", BigDecimal.valueOf( 50 ) },
                { "-10 * -5", BigDecimal.valueOf( 50 ) },
                { "(-10) * (-5)", BigDecimal.valueOf( 50 ) },
                { "(10 + 20) * (-5 * 3)", BigDecimal.valueOf( -450 ) },
                { "10/5", BigDecimal.valueOf( 2 ) },
                { "-10 / -5", BigDecimal.valueOf( 2 ) },
                { "(-10) / (-5)", BigDecimal.valueOf( 2 ) },
                { "(10 + 20) / (-5 * 3)", BigDecimal.valueOf( -2 ) },
                { "(10 + 20) / 0", null },
                { "10 ** 5", BigDecimal.valueOf( 100000 ) },
                { "10 ** -5", new BigDecimal( "0.00001" ) },
                { "(5+2) ** 5", BigDecimal.valueOf( 16807 ) },
                { "5+2 ** 5", BigDecimal.valueOf( 37 ) },
                { "5+2 ** 5+3", BigDecimal.valueOf( 40 ) },
                { "5+2 ** (5+3)", BigDecimal.valueOf( 261 ) },
                { "10 + null", null },
                { "null + 10", null },
                { "10 - null", null },
                { "null - 10", null },
                { "10 * null", null },
                { "null * 10", null },
                { "10 / null", null },
                { "null / 10", null },
                { "10 + 20 / -5 - 3", BigDecimal.valueOf( 3 ) },
                { "10 + 20 / ( -5 - 3 )", BigDecimal.valueOf( 7.5 ) },
                { "1.2*10**3", BigDecimal.valueOf( 1200.0 ) }
        };
        return Arrays.asList( cases );
    }
}
