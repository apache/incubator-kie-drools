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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;

public class FEELValuesConstantsTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // constants
                { "null", null , null},
                {"true", Boolean.TRUE , null},
                { "false", Boolean.FALSE , null},
                // dash is an unary test that always matches, so for now, returning true.
                // have to double check to know if this is not the case
                { "-", null, FEELEvent.Severity.ERROR },
                { ".872", new BigDecimal( "0.872" ) , null},
                { ".872e+21", new BigDecimal( "0.872e+21" ) , null},
                { ".872E+21", new BigDecimal( "0.872e+21" ) , null},
                { ".872e-21", new BigDecimal( "0.872e-21" ) , null},
                { ".872E-21", new BigDecimal( "0.872e-21" ) , null},
                { "-.872", new BigDecimal( "-0.872" ) , null},
                { "-.872e+21", new BigDecimal( "-0.872e+21" ) , null},
                { "-.872E+21", new BigDecimal( "-0.872e+21" ) , null},
                { "-.872e-21", new BigDecimal( "-0.872e-21" ) , null},
                { "-.872E-21", new BigDecimal( "-0.872e-21" ) , null},
                { "+.872", new BigDecimal( "0.872" ) , null},
                { "+.872e+21", new BigDecimal( "0.872e+21" ) , null},
                { "+.872E+21", new BigDecimal( "0.872e+21" ) , null},
                { "+.872e-21", new BigDecimal( "0.872e-21" ) , null},
                { "+.872E-21", new BigDecimal( "0.872e-21" ) , null},

                { "50", new BigDecimal( "50" ) , null},
                { "50e+21", new BigDecimal( "50e+21" ) , null},
                { "50E+21", new BigDecimal( "50e+21" ) , null},
                { "50e-21", new BigDecimal( "50e-21" ) , null},
                { "50E-21", new BigDecimal( "50e-21" ) , null},
                { "-50", new BigDecimal( "-50" ) , null},
                { "-50e+21", new BigDecimal( "-50e+21" ) , null},
                { "-50E+21", new BigDecimal( "-50e+21" ) , null},
                { "-50e-21", new BigDecimal( "-50e-21" ) , null},
                { "-50E-21", new BigDecimal( "-50e-21" ) , null},
                { "+50", new BigDecimal( "50" ) , null},
                { "+50e+21", new BigDecimal( "50e+21" ) , null},
                { "+50E+21", new BigDecimal( "50e+21" ) , null},
                { "+50e-21", new BigDecimal( "50e-21" ) , null},
                { "+50E-21", new BigDecimal( "50e-21" ) , null},
                { "50.872", new BigDecimal( "50.872" ) , null},
                { "50.872e+21", new BigDecimal( "50.872e+21" ) , null},
                { "50.872E+21", new BigDecimal( "50.872e+21" ) , null},
                { "50.872e-21", new BigDecimal( "50.872e-21" ) , null},
                { "50.872E-21", new BigDecimal( "50.872e-21" ) , null},
                { "-50.567", new BigDecimal( "-50.567" ) , null},
                { "-50.872e+21", new BigDecimal( "-50.872e+21" ) , null},
                { "-50.872E+21", new BigDecimal( "-50.872e+21" ) , null},
                { "-50.872e-21", new BigDecimal( "-50.872e-21" ) , null},
                { "-50.872E-21", new BigDecimal( "-50.872e-21" ) , null},
                { "+50.567", new BigDecimal( "50.567" ) , null},
                { "+50.872e+21", new BigDecimal( "50.872e+21" ) , null},
                { "+50.872E+21", new BigDecimal( "50.872e+21" ) , null},
                { "+50.872e-21", new BigDecimal( "50.872e-21" ) , null},
                { "+50.872E-21", new BigDecimal( "50.872e-21" ) , null},
                // quotes are a syntactical markup character for strings, so they disappear when the expression is evaluated
                { "\"foo bar\"", "foo bar" , null},
                { "\"šomeÚnicodeŠtriňg\"", "šomeÚnicodeŠtriňg" , null},
                { "\"横綱\"", "横綱" , null},
                { "\"thisIsSomeLongStringThatMustBeProcessedSoHopefullyThisTestPassWithItAndIMustWriteSomethingMoreSoItIsLongerAndLongerAndLongerAndLongerAndLongerTillItIsReallyLong\"", "thisIsSomeLongStringThatMustBeProcessedSoHopefullyThisTestPassWithItAndIMustWriteSomethingMoreSoItIsLongerAndLongerAndLongerAndLongerAndLongerTillItIsReallyLong" , null},
                { "\"\"", "" , null},
                { "-\"10\"", null , FEELEvent.Severity.ERROR},
                { "-string(\"10\")", null , FEELEvent.Severity.ERROR},
                { "+\"10\"", null , FEELEvent.Severity.ERROR},
        };
        return addAdditionalParameters(cases, false);
    }
}
