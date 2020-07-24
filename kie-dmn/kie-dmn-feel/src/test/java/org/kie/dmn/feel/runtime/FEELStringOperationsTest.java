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

import java.util.Collection;

import org.junit.runners.Parameterized;

public class FEELStringOperationsTest extends BaseFEELTest {
    
    /**
     * WARNING: do not use as JUNit's @Parameters name the index {1} within this test class, as this would result in invalid character in the XML surefire-report
     * Original error was: An invalid XML character (Unicode: 0x8) was found in the value of attribute "name" and element is "testcase".
     */
    @Parameterized.Parameters(name = "{index}: {0} ")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // string concatenation
                { "\"foo\"+\"bar\"", "foobar" , null},
                // string escapes
                { "\"string with \\\"quotes\\\"\"", "string with \"quotes\"", null},
                { "\"a\\b\\t\\n\\f\\r\\\"\\'\\\\\\u2202b\"", "a\b\t\n\f\r\"\'\\\u2202b", null},
                {"string length(\"foo\") = 3", Boolean.TRUE, null},
                {"string length(\"🐎ab\") = 3", Boolean.TRUE, null},
                {"string length(\"\uD83D\uDC0Eab\") = 3", Boolean.TRUE, null},
                {"string length(\"\\uD83D\\uDC0Eab\") = 3", Boolean.TRUE, null},
                {"substring(\"🐎ab\", 2) = \"ab\"", Boolean.TRUE, null},
                {"substring(\"foobar\",3) = \"obar\"", Boolean.TRUE, null},
                {"substring(\"foobar\",3,3) =\"oba\" ", Boolean.TRUE, null},
                {"substring(\"foobar\", -2, 1) = \"a\"", Boolean.TRUE, null},
                {"substring(\"\\U01F40Eab\", 2) = \"ab\"", Boolean.TRUE, null},
        };
        return addAdditionalParameters(cases, false);
    }
}
