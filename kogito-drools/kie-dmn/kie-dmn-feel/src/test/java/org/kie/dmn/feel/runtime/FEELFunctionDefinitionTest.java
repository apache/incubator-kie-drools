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

public class FEELFunctionDefinitionTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // function definition and invocation
                {"{ hello world : function() \"Hello World!\", message : hello world() }.message", "Hello World!" },
                {"{ is minor : function( person's age ) person's age < 18, bob is minor : is minor( 16 ) }.bob is minor", Boolean.TRUE },
                {"{ maximum : function( v1, v2 ) external { java : { class : \"java.lang.Math\", method signature: \"max(long,long)\" } }, the max : maximum( 10, 20 ) }.the max",
                        BigDecimal.valueOf( 20 ) },
                // variable number of parameters
                {"{ \n"
                 + "    string format : function( mask, value ) external {\n"
                 + "                      java : {\n"
                 + "                          class : \"java.lang.String\",\n"
                 + "                          method signature : \"format( java.lang.String, [Ljava.lang.Object; )\"\n"
                 + "                      }\n"
                 + "                  },\n"
                 + "    format currency : function( amount ) \n"
                 + "                 string format( \"$%,4.2f\", amount )\n"
                 + "    ,\n"
                 + "   result : format currency( 76499.3456 )\n"
                 + "}.result",
                 "$76,499.35"
                }
        };
        return Arrays.asList( cases );
    }
}
