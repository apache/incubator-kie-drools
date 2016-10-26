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
import java.util.stream.Collectors;
import org.junit.runners.Parameterized;

public class FEELConditionsAndLoopsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // if expressions
                { "if true then 10+5 else 10-5", BigDecimal.valueOf( 15 ) },
                { "if false then \"foo\" else \"bar\"", "bar" },
                { "if date(\"2016-08-02\") > date(\"2015-12-25\") then \"yey\" else \"nay\"", "yey" },
                { "if null then \"foo\" else \"bar\"", null },

                // for
                {"for x in [ 10, 20, 30 ], y in [ 1, 2, 3 ] return x * y",
                        Arrays.asList( 10, 20, 30, 20, 40, 60, 30, 60, 90 ).stream().map( x -> BigDecimal.valueOf( x ) ).collect( Collectors.toList() ) }

        };
        return Arrays.asList( cases );
    }
}
