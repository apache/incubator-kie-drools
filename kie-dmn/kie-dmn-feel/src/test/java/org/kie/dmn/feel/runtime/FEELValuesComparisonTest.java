/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;

public class FEELValuesComparisonTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // number comparisons
                { "10.4 < 20.6", Boolean.TRUE , null},
                { "10.4 <= 20.6", Boolean.TRUE , null},
                { "10.4 = 20.6", Boolean.FALSE , null},
                { "10.4 != 20.6", Boolean.TRUE , null},
                { "10.4 > 20.6", Boolean.FALSE , null},
                { "10.4 >= 20.6", Boolean.FALSE , null},
                { "15.25 = 15.25", Boolean.TRUE , null},
                { "15.25 != 15.25", Boolean.FALSE , null},

                // string comparisons
                { "\"foo\" < \"bar\"", Boolean.FALSE , null},
                { "\"foo\" <= \"bar\"", Boolean.FALSE , null},
                { "\"foo\" = \"bar\"", Boolean.FALSE , null},
                { "\"foo\" != \"bar\"", Boolean.TRUE , null},
                { "\"foo\" > \"bar\"", Boolean.TRUE , null},
                { "\"foo\" >= \"bar\"", Boolean.TRUE , null},
                { "\"foo\" = \"foo\"", Boolean.TRUE , null},
                { "\"foo\" != \"foo\"", Boolean.FALSE , null},

                // boolean comparisons
                { "true = true", Boolean.TRUE , null},
                { "false = false", Boolean.TRUE , null},
                { "false = true", Boolean.FALSE , null},
                { "true = false", Boolean.FALSE , null},
                { "true != true", Boolean.FALSE , null},
                { "false != false", Boolean.FALSE , null},
                { "false != true", Boolean.TRUE , null},
                { "true != false", Boolean.TRUE , null},
                
                // other comparisons
                { "duration(\"P1Y\") < duration(\"P2Y\")", Boolean.TRUE , null},
                { "duration(\"P1Y\") > duration(\"P2Y\")", Boolean.FALSE , null},

                // other types of equalities
                { "[ 1..3 ] = [ 1..3 ]", Boolean.TRUE , null},
                { "[ \"1\"..\"3\" ] = [ \"1\"..\"3\" ]", Boolean.TRUE , null},
                { "[\"1978-09-12\"..\"1978-10-12\"] = [\"1978-09-12\"..\"1978-10-12\"]", Boolean.TRUE, null},
                { "[ 1, 2, 3] = [1, 2, 3]", Boolean.TRUE , null},
                { "[ 1, 2, 3, 4] = [1, 2, 3]", Boolean.FALSE , null},
                { "[ 1, 2, 3] = [1, \"foo\", 3]", Boolean.FALSE , null},
                { "{ x : \"foo\" } = { x : \"foo\" }", Boolean.TRUE , null},
                { "{ x : \"foo\", y : [1, 2] } = { x : \"foo\", y : [1, 2] }", Boolean.TRUE , null},
                { "{ x : \"foo\", y : [1, 2] } = { y : [1, 2], x : \"foo\" }", Boolean.TRUE , null},
                { "{ x : \"foo\", y : [1, 2] } = { y : [1], x : \"foo\" }", Boolean.FALSE , null},
                { "{ x : \"foo\", y : { z : 1, w : 2 } } = { y : { z : 1, w : 2 }, x : \"foo\" }", Boolean.TRUE , null},
                { "[ 1, 2, 3] != [1, 2, 3]", Boolean.FALSE , null},
                { "[ 1, 2, 3, 4] != [1, 2, 3]", Boolean.TRUE , null},
                { "[ 1, 2, 3] != [1, \"foo\", 3]", Boolean.TRUE , null},
                { "{ x : \"foo\" } != { x : \"foo\" }", Boolean.FALSE , null},
                { "{ x : \"foo\", y : [1, 2] } != { x : \"foo\", y : [1, 2] }", Boolean.FALSE , null},
                { "{ x : \"foo\", y : [1, 2] } != { y : [1, 2], x : \"foo\" }", Boolean.FALSE , null},
                { "{ x : \"foo\", y : [1, 2] } != { y : [1], x : \"foo\" }", Boolean.TRUE , null},
                { "{ x : \"foo\", y : { z : 1, w : 2 } } != { y : { z : 1, w : 2 }, x : \"foo\" }", Boolean.FALSE , null},

                // null comparisons and comparisons between different types
                { "10.4 < null", null , null},
                { "null <= 30.6", null , null},
                { "40 > null", null , null},
                { "null >= 30", null , null},
                { "\"foo\" > null", null , null},
                { "10 > \"foo\"", null , null},
                { "false > \"foo\"", null , null},
                { "\"bar\" != true", null , null},
                { "null = \"bar\"", Boolean.FALSE , null},
                { "false != null", Boolean.TRUE , null},
                { "null = true", Boolean.FALSE , null},
                { "12 = null", Boolean.FALSE, null},
                { "12 != null", Boolean.TRUE, null},
                { "null = null", Boolean.TRUE , null},
                { "null != null", Boolean.FALSE , null},

                // RHDM-1119 
                { "{ m: <18 }.m(16)", true, null}, // Working expression, for the expr raising compilation Warn test have been moved.
                { "{list : 1, r: list < 3}.r", Boolean.TRUE , null}, // strange name for a number literal, intended this way.
                { "{list : 1, r: list< 3}.r", Boolean.TRUE , null}, 
                { "{context : 1, r: context < 3}.r", Boolean.TRUE , null}, // strange name for a number literal, intended this way.
                { "{context : 1, r: context< 3}.r", Boolean.TRUE , null}, 
        };
        return addAdditionalParameters(cases, false);
    }
}
