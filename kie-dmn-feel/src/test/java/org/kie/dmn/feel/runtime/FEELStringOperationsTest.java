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

public class FEELStringOperationsTest extends BaseFEELTest {
    
    /**
     * WARNING: do not use as JUNit's @Parameters name the index {1} within this test class, as this would result in invalid character in the XML surefire-report
     * Original error was: An invalid XML character (Unicode: 0x8) was found in the value of attribute "name" and element is "testcase".
     */
    @Parameterized.Parameters(name = "{index}: {0} ")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // string concatenation
                { "\"foo\"+\"bar\"", "foobar" },
                // string escapes
                { "\"string with \\\"quotes\\\"\"", "string with \"quotes\""},
                { "\"a\\b\\t\\n\\f\\r\\\"\\'\\\\\\u2202b\"", "a\b\t\n\f\r\"\'\\\u2202b"}
        };
        return Arrays.asList( cases );
    }
}

