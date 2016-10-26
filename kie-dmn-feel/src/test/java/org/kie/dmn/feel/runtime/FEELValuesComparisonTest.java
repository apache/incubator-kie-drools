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

public class FEELValuesComparisonTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // number comparisons
                { "10.4 < 20.6", Boolean.TRUE },
                { "10.4 <= 20.6", Boolean.TRUE },
                { "10.4 = 20.6", Boolean.FALSE },
                { "10.4 != 20.6", Boolean.TRUE },
                { "10.4 > 20.6", Boolean.FALSE },
                { "10.4 >= 20.6", Boolean.FALSE },
                { "15.25 = 15.25", Boolean.TRUE },
                { "15.25 != 15.25", Boolean.FALSE },

                // string comparisons
                { "\"foo\" < \"bar\"", Boolean.FALSE },
                { "\"foo\" <= \"bar\"", Boolean.FALSE },
                { "\"foo\" = \"bar\"", Boolean.FALSE },
                { "\"foo\" != \"bar\"", Boolean.TRUE },
                { "\"foo\" > \"bar\"", Boolean.TRUE },
                { "\"foo\" >= \"bar\"", Boolean.TRUE },
                { "\"foo\" = \"foo\"", Boolean.TRUE },
                { "\"foo\" != \"foo\"", Boolean.FALSE },

                // boolean comparisons
                { "true = true", Boolean.TRUE },
                { "false = false", Boolean.TRUE },
                { "false = true", Boolean.FALSE },
                { "true = false", Boolean.FALSE },
                { "true != true", Boolean.FALSE },
                { "false != false", Boolean.FALSE },
                { "false != true", Boolean.TRUE },
                { "true != false", Boolean.TRUE },

                // other types of equalities
                { "[ 1..3 ] = [ 1..3 ]", Boolean.TRUE },
                { "[ \"1\"..\"3\" ] = [ \"1\"..\"3\" ]", Boolean.TRUE },
                { "[\"1978-09-12\"..\"1978-10-12\"] = [\"1978-09-12\"..\"1978-10-12\"]", Boolean.TRUE},
                { "[ 1, 2, 3] = [1, 2, 3]", Boolean.TRUE },
                { "[ 1, 2, 3, 4] = [1, 2, 3]", Boolean.FALSE },
                { "[ 1, 2, 3] = [1, \"foo\", 3]", Boolean.FALSE },
                { "{ x : \"foo\" } = { x : \"foo\" }", Boolean.TRUE },
                { "{ x : \"foo\", y : [1, 2] } = { x : \"foo\", y : [1, 2] }", Boolean.TRUE },
                { "{ x : \"foo\", y : [1, 2] } = { y : [1, 2], x : \"foo\" }", Boolean.TRUE },
                { "{ x : \"foo\", y : [1, 2] } = { y : [1], x : \"foo\" }", Boolean.FALSE },
                { "{ x : \"foo\", y : { z : 1, w : 2 } } = { y : { z : 1, w : 2 }, x : \"foo\" }", Boolean.TRUE },
                { "[ 1, 2, 3] != [1, 2, 3]", Boolean.FALSE },
                { "[ 1, 2, 3, 4] != [1, 2, 3]", Boolean.TRUE },
                { "[ 1, 2, 3] != [1, \"foo\", 3]", Boolean.TRUE },
                { "{ x : \"foo\" } != { x : \"foo\" }", Boolean.FALSE },
                { "{ x : \"foo\", y : [1, 2] } != { x : \"foo\", y : [1, 2] }", Boolean.FALSE },
                { "{ x : \"foo\", y : [1, 2] } != { y : [1, 2], x : \"foo\" }", Boolean.FALSE },
                { "{ x : \"foo\", y : [1, 2] } != { y : [1], x : \"foo\" }", Boolean.TRUE },
                { "{ x : \"foo\", y : { z : 1, w : 2 } } != { y : { z : 1, w : 2 }, x : \"foo\" }", Boolean.FALSE },

                // null comparisons and comparisons between different types
                { "10.4 < null", null },
                { "null <= 30.6", null },
                { "40 > null", null },
                { "null >= 30", null },
                { "\"foo\" > null", null },
                { "10 > \"foo\"", null },
                { "false > \"foo\"", null },
                { "\"bar\" != true", null },
                { "null = \"bar\"", Boolean.FALSE },
                { "false != null", Boolean.TRUE },
                { "null = true", Boolean.FALSE },
                { "12 = null", Boolean.FALSE},
                { "12 != null", Boolean.TRUE},
                { "null = null", Boolean.TRUE },
                { "null != null", Boolean.FALSE }
        };
        return Arrays.asList( cases );
    }
}
