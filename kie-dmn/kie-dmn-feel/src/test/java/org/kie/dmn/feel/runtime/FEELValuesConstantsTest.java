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
import org.kie.dmn.api.feel.runtime.events.FEELEvent;

public class FEELValuesConstantsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // constants
                { "null", null , null},
                {"true", Boolean.TRUE , null},
                { "false", Boolean.FALSE , null},
                // dash is an unary test that always matches, so for now, returning true.
                // have to double check to know if this is not the case
                { "-", UnaryTest.class , null},
                { ".872", new BigDecimal( "0.872" ) , null},
                { "-.872", new BigDecimal( "-0.872" ) , null},
                { "+.872", new BigDecimal( "0.872" ) , null},
                { "50", new BigDecimal( "50" ) , null},
                { "-50", new BigDecimal( "-50" ) , null},
                { "+50", new BigDecimal( "50" ) , null},
                { "50.872", new BigDecimal( "50.872" ) , null},
                { "-50.567", new BigDecimal( "-50.567" ) , null},
                { "+50.567", new BigDecimal( "50.567" ) , null},
                // quotes are a syntactical markup character for strings, so they disappear when the expression is evaluated
                { "\"foo bar\"", "foo bar" , null},
                { "\"šomeÚnicodeŠtriňg\"", "šomeÚnicodeŠtriňg" , null},
                { "\"横綱\"", "横綱" , null},
                { "\"thisIsSomeLongStringThatMustBeProcessedSoHopefullyThisTestPassWithItAndIMustWriteSomethingMoreSoItIsLongerAndLongerAndLongerAndLongerAndLongerTillItIsReallyLong\"", "thisIsSomeLongStringThatMustBeProcessedSoHopefullyThisTestPassWithItAndIMustWriteSomethingMoreSoItIsLongerAndLongerAndLongerAndLongerAndLongerTillItIsReallyLong" , null},
                { "\"\"", "" , null}
        };
        return Arrays.asList( cases );
    }
}
