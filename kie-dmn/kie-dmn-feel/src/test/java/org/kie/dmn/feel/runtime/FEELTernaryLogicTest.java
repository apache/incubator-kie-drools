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

public class FEELTernaryLogicTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // ternary logic operations as per the spec
                { "true and true", Boolean.TRUE , null},
                { "true and false", Boolean.FALSE , null},
                { "true and null",  null , null},
                { "false and true", Boolean.FALSE , null},
                { "false and false", Boolean.FALSE , null},
                { "false and null", Boolean.FALSE , null},
                { "null and true", null , null},
                { "null and false", Boolean.FALSE , null},
                { "null and null", null , null},
                { "true or true", Boolean.TRUE , null},
                { "true or false", Boolean.TRUE , null},
                { "true or null",  Boolean.TRUE , null},
                { "false or true", Boolean.TRUE , null},
                { "false or false", Boolean.FALSE , null},
                { "false or null", null , null},
                { "null or true", Boolean.TRUE , null},
                { "null or false", null , null},
                { "null or null", null , null},
                // logical operator priority
                { "false and false or true", Boolean.TRUE , null},
                { "false and (false or true)", Boolean.FALSE , null},
                { "true or false and false", Boolean.TRUE , null},
                { "(true or false) and false", Boolean.FALSE , null}
        };
        return addAdditionalParameters(cases, false);
    }
}
