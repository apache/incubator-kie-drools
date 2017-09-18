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

public class FEELFunctionDefinitionTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // function definition and invocation
                {"{ hello world : function() \"Hello World!\", message : hello world() }.message", "Hello World!", null },
                {"{ functioncontext: { innercontext: {hello world : function() \"Hello World!\"}}, " +
                        " message : functioncontext.innercontext.hello world() }.message", "Hello World!", null },
                {"{ hello world : function() \"Hello World!\", message : helloWorld() }.message", null, FEELEvent.Severity.ERROR },
                {"{ is minor : function( person's age ) person's age < 18, bob is minor : is minor( 16 ) }.bob is minor", Boolean.TRUE, null },
                {"{ is minor : function( person's age ) person's age < 18, bob is minor : is minor( 16, 24 ) }.bob is minor", null, FEELEvent.Severity.ERROR },

                // Tests for FunctionDefNode.convertPrimitiveNameToType
                {"{ abs : function( v1 ) external { java : { class : \"java.lang.Math\", method signature: \"abs(double)\" } }, absolute : abs( -10.1 ) }.absolute",
                        BigDecimal.valueOf(10.1), null },
                {"{ abs : function( v1 ) external { java : { class : \"java.lang.Math\", method signature: \"abs(float)\" } }, absolute : abs( -10.1 ) }.absolute",
                        BigDecimal.valueOf(10.1), null },
                {"{ abs : function( v1 ) external { java : { class : \"java.lang.Math\", method signature: \"abs(int)\" } }, absolute : abs( -10 ) }.absolute",
                        BigDecimal.valueOf(10), null },
                {"{ compare shorts : function( v1, v2 ) external { java : { class : \"java.lang.Short\", method signature: \"compare(short,short)\" } }, compareResult : compare shorts( 10, 10 ) }.compareResult",
                        BigDecimal.valueOf(0), null },
                {"{ compare bytes : function( v1, v2 ) external { java : { class : \"java.lang.Byte\", method signature: \"compare(byte,byte)\" } }, compareResult : compare bytes( 10, 10 ) }.compareResult",
                        BigDecimal.valueOf(0), null },
                {"{ compare chars : function( v1, v2 ) external { java : { class : \"java.lang.Character\", method signature: \"compare(char,char)\" } }, compareResult : compare chars( \"a\", \"a\" ) }.compareResult",
                        BigDecimal.valueOf(0), null },
                {"{ compare booleans : function( v1, v2 ) external { java : { class : \"java.lang.Boolean\", method signature: \"compare(boolean,boolean)\" } }, compareResult : compare booleans( true, true ) }.compareResult",
                        BigDecimal.valueOf(0), null },

                // General tests for external functions
                {"{ maximum : function( v1, v2 ) external { java : { class : \"java.lang.Math\", method signature: \"max(long,long)\" } }, the max : maximum( 10, 20 ) }.the max",
                        BigDecimal.valueOf( 20 ), null },
                {"{ maximum : function( v1, v2 ) external { java : { class : \"java.lang.Math\", method signature: \"max(long,long,int)\" } }, the max : maximum( 10, 20 ) }.the max",
                        null, FEELEvent.Severity.ERROR },
                {"{ maximum : function( v1, v2 ) external { java : { class : \"java.lang.Math\", method signature: \"maxX(long,long,int)\" } }, the max : maximum( 10, 20 ) }.the max",
                        null, FEELEvent.Severity.ERROR },
                {"{ maximum : function( v1, v2 ) external { }, the max : maximum( 10, 20 ) }.the max", null, FEELEvent.Severity.ERROR },
                {"{ maximum : function( v1, v2 ) external { missingDefiniton }, the max : maximum( 10, 20 ) }.the max", null, FEELEvent.Severity.ERROR },
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
                 "$76,499.35", null
                }
        };
        return Arrays.asList( cases );
    }
}
