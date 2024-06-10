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

public class FEELFunctionDefinitionTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // function definition and invocation
                {"{ hello : function() \"Hello World!\"}.hello()", "Hello World!", null},
                {"{ hello : function(n) \"Hello \"+n+\"!\"}.hello(\"John\")", "Hello John!", null},
                {"{ hello : function( n : string ) \"Hello \"+n+\"!\"}.hello(\"John\")", "Hello John!", null},
                {"{ idfn : function( arg ) arg, r : idfn(\"asd\") }.r", "asd", null},
                {"{ idfn : function( arg ) arg, r : idfn(123) }.r", BigDecimal.valueOf(123), null},
                {"{ idfn : function( arg : string ) arg, r : idfn(\"asd\") }.r", "asd", null},
                {"{ idfn : function( arg : string ) arg, r : idfn(123) }.r", null, FEELEvent.Severity.WARN},
                {"{ hello world : function() \"Hello World!\", message : hello world() }.message", "Hello World!", null},
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
                {"{ maximum : function( v1, v2 ) external { missingDefiniton : }, the max : maximum( 10, 20 ) }.the max", null, FEELEvent.Severity.ERROR },
                {"{ something : \"hello world\" }.something()", null, FEELEvent.Severity.ERROR },
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
                },
                {"{ myimport : { f1 : function() \"Hi\", x1 : function(name) f1() + \" \" + name }, r1 : myimport.x1(\"John\") }.r1", "Hi John", null },
                {"{ myimport : { f1 : function() \"Hi\", f2 : function() f1() + \" \" , x1 : function(name) f2() + name }, r1 : myimport.x1(\"John\") }.r1", "Hi John", null },
                {"{ m : { n : { o : { f1 : function() \"Hi\", f2 : function() f1() + \" \" , x1 : function(name) f2() + name }}}, r1 : m.n.o.x1(\"John\") }.r1", "Hi John", null },
                {"{ m : { n : { f1 : function() \"Hi\", f2 : function() f1() + \" \" , o : { x1 : function(name) f2() + name }}}, r1 : m.n.o.x1(\"John\") }.r1", "Hi John", null },
                {"{ m : { n : { f1 : function() \"Hi\", f2 : function() f1() + \" \" , o : { x1 : function(name) f2() + name }}}, r2 : m.n.f1() }.r2", "Hi", null },
                {"{ f : function(a) function(b) a + b, r : f(1)(2) }.r", BigDecimal.valueOf(3), null },
                {"{ a: 9, b: 9, f : function(a) function(b) a + b, r : f(1)(2) }.r", BigDecimal.valueOf(3), null },
                {"{ Y: function(f) (function(x) x(x))(function(y) f(function(x) y(y)(x))), fac: Y(function(f) function(n) if n > 1 then n * f(n-1) else 1), fac4: fac(4) }.fac4", BigDecimal.valueOf(24), null }
        };
        return addAdditionalParameters(cases, false);
    }
}
