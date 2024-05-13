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


public class BFEELTest extends BaseFEELTest {

    /**
     * WARNING: do not use as JUNit's @Parameters name the index {1} within this test class, as this would result in invalid character in the XML surefire-report
     * Original error was: An invalid XML character (Unicode: 0x8) was found in the value of attribute "name" and element is "testcase".
     */
    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, BaseFEELTest.FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                {"\"a\" = 1", false, null},
                {"\"a\" != 1", true, null},
                {"\"a\" < 1", false, null},
                {"\"a\" <= null", false, null},
                {"\"a\" > 1", false, null},
                {"null >= 1", false, null},
                {"not(\"a\")", false, null},
                {"true and \"x\"", false, null},
                {"false or \"x\"", false, null},
                {"\"a\" in [1..100]", false, null},
                {"null between 1 and 100", false, null},
//                {"string(null)", "", null},
//                {"substring(\"a\", \"z\")", "", FEELEvent.Severity.ERROR},
        };
        return addAdditionalParameters(cases, false, FEELDialect.BFEEL);
    }
}
