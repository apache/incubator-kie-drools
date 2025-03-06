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
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;

public class FEELExpressionsTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // quantified expressions
                { "some price in [ 80, 11, 110 ] satisfies price > 100", Boolean.TRUE , null},
                { "some price in [ 80, 11, 90 ] satisfies price > 100", Boolean.FALSE , null},
                { "some x in [ 5, 6, 7 ], y in [ 10, 11, 6 ] satisfies x > y", Boolean.TRUE , null},
                { "every price in [ 80, 11, 90 ] satisfies price > 10", Boolean.TRUE , null},
                { "every price in [ 80, 11, 90 ] satisfies price > 70", Boolean.FALSE , null},
                { "some x in [ 5, 6, 7 ], y in [ 10, 11, 12 ] satisfies x < y", Boolean.TRUE , null},
                { "some price in [ 80, 11, 110 ] satisfies price > max(100, 50, 10)", Boolean.TRUE , null},

                // path expressions
                {"{ full name: { first name: \"John\", last name: \"Doe\" } }.full name.last name", "Doe" , null},

                // filter expressions with proper precedence
                {"{ EmployeeTable : [ \n"+
                 "    { id : \"333\", dept : 1, name : \"Aaron\" },\n" +
                 "    { id : \"444\", dept : 1, name : \"Bob\" }, \n" +
                 "    { id : \"555\", dept : 2, name : \"Clark\" } ],\n" +
                 "  DeptTable : [ \n" +
                 "    { number : 1, name : \"Sales\", manager : \"Jack\" },\n" +
                 "    { number : 2, name : \"Engineering\", manager : \"Susie\" } ],\n" +
                 "  Dept : EmployeeTable[ name = \"Clark\" ].dept[1],\n" +
                 "  Manager : DeptTable[ number = Dept ].manager[1],\n" +
                 "  ManagerInline : DeptTable[ number = EmployeeTable[ name = \"Clark\" ].dept[1] ].manager[1]\n"+
                 "}.ManagerInline", "Susie", null },

                // named parameters: in this case foo is null
                {"{ is minor : function( foo, person's age ) foo = null and person's age < 18, bob is minor : is minor( person's age : 16 ) }.bob is minor", Boolean.TRUE , null},
                {"{ is eighteen : function( foo, person's age ) foo = null and person's age = 18, bob is eighteen : is eighteen( person's age : 18 ) }.bob is eighteen", Boolean.TRUE , null},
                {"{ is not eighteen : function( foo, person's age ) foo = null and person's age != 18, bob is not eighteen : is not eighteen( person's age : 17 ) }.bob is not eighteen", Boolean.TRUE , null},

                // unary test invocation
                {"{ is minor : < 18, bob is minor : is minor(16) }.bob is minor", Boolean.TRUE , null},
                {"{ is eighteen : = 18, bob is eighteen : is eighteen(18) }.bob is eighteen", Boolean.TRUE , null},
                {"{ is eighteen : = 18, bob is eighteen : is eighteen(17) }.bob is eighteen", Boolean.FALSE , null},
                {"{ is not eighteen : != 18, bob is not eighteen : is not eighteen(18) }.bob is not eighteen", Boolean.FALSE , null},
                {"{ is not eighteen : != 18, bob is not eighteen : is not eighteen(17) }.bob is not eighteen", Boolean.TRUE , null},

                // negated unary tests
                {"10 in ( not( <5, >=20, =15, !=10 ) )", Boolean.FALSE, FEELEvent.Severity.ERROR},
                {"\"Boston\" in ( not( \"Toronto\", \"Montreal\" ) )", Boolean.FALSE , FEELEvent.Severity.ERROR},

                // Unary tests with ? character
                {"{ ? foo : 5, result : ? foo < 10 }.result", Boolean.TRUE , null},
                {"{ ? foo : 5, ? bar : 10, result : ? foo < ? bar}.result", Boolean.TRUE , null},
                {"{ ?foo : 5, result : ?foo < 10 }.result", Boolean.TRUE , null},
                {"{ foo ? : 5, result : foo ? < 10 }.result", Boolean.TRUE , null},
                {"{ foo?bar : 5, result : foo?bar < 10 }.result", Boolean.TRUE , null},

                // unary tests with context evaluation, i.e., the test is defined before the variable "x"
                {"{ test : > x, y : 20, x : 10, result : y in ( test ) }.result", Boolean.FALSE, FEELEvent.Severity.ERROR},
                {"{ test : > x, y : 20, x : 10, result : test( y ) }.result", null, FEELEvent.Severity.ERROR},
                {"{ x : 10, test : > x, y : 20, result : y in ( test ) }.result", Boolean.TRUE , null},
                {"{ x : 10, test : > x, y : 20, result : test( y ) }.result", Boolean.TRUE , null},
                {"{ test : in x, y : 20, x : [10, 20, 30], result : test( y ) }.result", null, FEELEvent.Severity.ERROR},

                {"2 in 2", Boolean.TRUE , null},
                {"{ x : 2, result : x in 2 }.result", Boolean.TRUE , null},
                {"{ someList : [1, 2, 3], result : 2 in someList }.result", Boolean.TRUE , null},
                {"{ someList : [1, 2, 3], x : 2, result : x in someList }.result", Boolean.TRUE , null},
                {"{ someNestedList : { theList : [1, 2, 3] } , x : 2, result : x in someNestedList.theList }.result", Boolean.TRUE , null},
                {"47 in 2", Boolean.FALSE , null},
                {"{ x : 47, result : x in 2 }.result", Boolean.FALSE , null},
                {"{ someList : [1, 2, 3], result : 47 in someList }.result", Boolean.FALSE , null},
                {"{ someList : [1, 2, 3], x : 47, result : x in someList }.result", Boolean.FALSE , null},
                {"{ someNestedList : { theList : [1, 2, 3] } , x : 47, result : x in someNestedList.theList }.result", Boolean.FALSE , null},
                {"{ exp: 2, v: 3, r: exp**v}.r", BigDecimal.valueOf(8), null},
                {"{Principal: 12, Rate: 1, Fees: 1, Term: -1, R: (Principal*Rate/12)/(1-(1+Rate/12)**-Term)+Fees}.R", new BigDecimal("-11.00000000000000000000000000000005"), null},
                {"3[item > 2]", List.of(new BigDecimal(3)), null},
                {"contains([\"foobar\"], \"of\")", Boolean.FALSE, null},

        };
        return addAdditionalParameters(cases, false);
    }
}
