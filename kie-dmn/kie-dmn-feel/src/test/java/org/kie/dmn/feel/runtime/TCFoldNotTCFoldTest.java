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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;

public class TCFoldNotTCFoldTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases =
                new Object[][]{
                               {"date(\"2021-02-12\")", LocalDate.of(2021, 2, 12), null},
                               {"{date : function(s) s, r: date(\"2021-02-12\")}.r", "2021-02-12", null},
                               {"{fs : [function(s) s], r: for date in fs return date(\"2021-02-12\")}.r[1]", "2021-02-12", null},
                               {"{fs : [function(s) s], r: every date in fs satisfies date(\"2021-02-12\") = \"2021-02-12\"}.r", Boolean.TRUE, null},
                               {"{f : function(date) date(\"2021-02-12\"), id : function(x) x, r: f(id)}.r", "2021-02-12", null},
                               {"date and time(\"2021-02-12T12:34:56\")", LocalDateTime.of(2021, 2, 12, 12, 34, 56), null},
                               {"{date and time : function(s) s, r: date and time(\"2021-02-12T12:34:56\")}.r", "2021-02-12T12:34:56", null},
                               {"{fs : [function(s) s], r: for date and time in fs return date and time(\"2021-02-12T12:34:56\")}.r[1]", "2021-02-12T12:34:56", null},
                               {"{fs : [function(s) s], r: every date and time in fs satisfies date and time(\"2021-02-12T12:34:56\") = \"2021-02-12T12:34:56\"}.r", Boolean.TRUE, null},
                               {"{f : function(date and time) date and time(\"2021-02-12T12:34:56\"), id : function(x) x, r: f(id)}.r", "2021-02-12T12:34:56", null},
        };
        final Collection<Object[]> c = addAdditionalParameters(cases, false);
        c.addAll(addAdditionalParameters(cases, true));
        return c;
    }
}
