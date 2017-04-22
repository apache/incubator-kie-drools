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

import java.util.Arrays;
import java.util.Collection;
import org.junit.runners.Parameterized;

public class FEELOperatorsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // 'not' expression
                { "not( true )", Boolean.FALSE },
                { "not( false )", Boolean.TRUE },
                { "not( 10 = 3 )", Boolean.TRUE },
                { "not( \"foo\" )", UnaryTest.class },

                // between
                { "10 between 5 and 12", Boolean.TRUE },
                { "10 between 20 and 30", Boolean.FALSE },
                { "\"foo\" between 5 and 12", null },
                { "\"foo\" between \"bar\" and \"zap\"", Boolean.TRUE },
                { "\"foo\" between null and \"zap\"", null },
                { "date(\"2016-08-02\") between date(\"2016-01-01\") and date(\"2016-12-31\")", Boolean.TRUE },

                // in operator
                { "10 in ( 3, 5*2, 20 )", Boolean.TRUE },
                { "null in ( 10, \"foo\", null )", Boolean.TRUE },
                { "\"foo\" in ( \"bar\", \"baz\" )", Boolean.FALSE },
                { "\"foo\" in null", null },
                { "\"foo\" in ( 10, false, \"foo\" )", Boolean.TRUE },
                { "10 in < 20", Boolean.TRUE },
                { "10 in ( > 50, < 5 )", Boolean.FALSE },
                { "10 in ( > 5, < -40 )", Boolean.TRUE },
                { "null in ( > 20, null )", Boolean.TRUE },
                { "null in -", Boolean.TRUE },
                { "10 in [5..20]", Boolean.TRUE },
                { "10 in [10..20)", Boolean.TRUE },
                { "10 in (10..20)", Boolean.FALSE },
                { "10 in (5..10)", Boolean.FALSE },
                { "10 in ]5..10[", Boolean.FALSE },
                { "10 in (5..10]", Boolean.TRUE },
                { "\"b\" in (\"a\"..\"z\"]", Boolean.TRUE },

                // instance of
                {"10 instance of number", Boolean.TRUE },
                {"\"foo\" instance of string", Boolean.TRUE },
                {"date(\"2016-08-11\") instance of date", Boolean.TRUE },
                {"time(\"23:59:00\") instance of time", Boolean.TRUE },
                {"date and time(\"2016-07-29T05:48:23.765-05:00\") instance of date and time", Boolean.TRUE },
                {"duration( \"P2Y2M\" ) instance of duration", Boolean.TRUE },
                {"true instance of boolean", Boolean.TRUE },
                {"< 10 instance of unary test", Boolean.TRUE },
                {"[10..20) instance of range", Boolean.TRUE },
                {"[10, 20, 30] instance of list", Boolean.TRUE },
                {"{ foo : \"foo\" } instance of context", Boolean.TRUE },
                {"null instance of unknown", Boolean.FALSE }, // See FEEL spec table 49.
                {"null instance of string", Boolean.FALSE },  // See FEEL spec table 49.
                {"\"foo\" instance of unknown", Boolean.TRUE }, 
                {"10 instance of unknown", Boolean.TRUE },
                {"duration instance of function", Boolean.TRUE }
        };
        return Arrays.asList( cases );
    }
}
