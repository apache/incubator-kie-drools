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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;

public class FEELConditionsAndLoopsTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // if expressions
                { "if true then 10+5 else 10-5", BigDecimal.valueOf( 15 ) , null},
                { "if false then \"foo\" else \"bar\"", "bar" , null},
                { "if date(\"2016-08-02\") > date(\"2015-12-25\") then \"yey\" else \"nay\"", "yey" , null},
                {"if null then \"foo\" else \"bar\"", "bar" , null },
                {"if \"xyz\" then \"foo\" else \"bar\"", "bar" , null },
                {"if true then if true then 1 else 2 else if true then 3 else 4", BigDecimal.valueOf( 1 ), null},
                {"1 + if true then 1 else 2", BigDecimal.valueOf( 2 ), null},

                // for
                {"for x in [ 10, 20, 30 ], y in [ 1, 2, 3 ] return x * y",
                        Stream.of(10, 20, 30, 20, 40, 60, 30, 60, 90 ).map(BigDecimal::valueOf).collect(Collectors.toList() ),
                 null },
                {"count( for x in [1, 2, 3] return x+1 )", BigDecimal.valueOf( 3 ), null},

                // quantified
                {"if every x in [ 1, 2, 3 ] satisfies x < 5 then \"foo\" else \"bar\"", "foo", null},
                // DROOLS-5927 short-circuit boolean ops
                {"null != null and string length(null) > 0", Boolean.FALSE, null}, // expect short-circuit behaviour
                {"null = null or string length(null) = 0", Boolean.TRUE, null}, // expect short-circuit behaviour

        };
        return addAdditionalParameters(cases, false);
    }
}
