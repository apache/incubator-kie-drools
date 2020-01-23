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
import org.kie.dmn.api.feel.runtime.events.FEELEvent;

public class FEELOperatorsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{3}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // 'not' expression
                { "not( true )", Boolean.FALSE , null},
                { "not( false )", Boolean.TRUE , null},
                { "not( 10 = 3 )", Boolean.TRUE , null},
                { "not(list contains([1,2,3,4,5,6], 3))", Boolean.FALSE , null},
                { "not( \"foo\" )", null, FEELEvent.Severity.ERROR},
                {"{x : 10, r : not( x = 3 )}.r", Boolean.TRUE, null},
                {"{x : 3, r : not( x = 3 )}.r", Boolean.FALSE, null},

                // between
                { "10 between 5 and 12", Boolean.TRUE , null},
                { "10 between 20 and 30", Boolean.FALSE , null},
                { "10 between 5 and \"foo\"", null , FEELEvent.Severity.ERROR},
                { "10 between 20 and \"foo\"", Boolean.FALSE , FEELEvent.Severity.ERROR},
                {"\"foo\" between 5 and 12", null , FEELEvent.Severity.ERROR},
                { "\"foo\" between \"bar\" and \"zap\"", Boolean.TRUE , null},
                { "\"foo\" between null and \"zap\"", null , FEELEvent.Severity.ERROR},
                { "date(\"2016-08-02\") between date(\"2016-01-01\") and date(\"2016-12-31\")", Boolean.TRUE , null},
                { "duration(\"P3Y\") between duration(\"P2Y\") and duration(\"P4Y\")", Boolean.TRUE , null},
                { "duration(\"P1Y\") between duration(\"P2Y\") and duration(\"P4Y\")", Boolean.FALSE , null},

                // in operator
                { "10 in ( 3, 5*2, 20 )", Boolean.TRUE , null},
                { "null in ( 10, \"foo\", null )", Boolean.TRUE , null},
                { "\"foo\" in ( \"bar\", \"baz\" )", Boolean.FALSE , null},
                { "\"foo\" in null", null , FEELEvent.Severity.ERROR},
                { "\"foo\" in ( 10, false, \"foo\" )", Boolean.TRUE , null},
                { "10 in < 20", Boolean.TRUE , null},
                { "10 in ( > 50, < 5 )", Boolean.FALSE , null},
                { "10 in ( > 5, < -40 )", Boolean.TRUE , null},
                { "null in ( > 20, null )", Boolean.TRUE , null},
                { "null in -", null, FEELEvent.Severity.ERROR},
                { "10 in [5..20]", Boolean.TRUE , null},
                { "10 in [10..20)", Boolean.TRUE , null},
                { "10 in (10..20)", Boolean.FALSE , null},
                { "10 in (5..10)", Boolean.FALSE , null},
                { "10 in ]5..10[", Boolean.FALSE , null},
                { "10 in (5..10]", Boolean.TRUE , null},
                { "\"b\" in (\"a\"..\"z\"]", Boolean.TRUE , null},
                {" duration(\"P1Y2M\") in [ duration(\"P1Y2M\") .. duration(\"P1Y3M\")] ", Boolean.TRUE, null},
                {" duration(\"P1Y4M\") in [ duration(\"P1Y2M\") .. duration(\"P1Y3M\")] ", Boolean.FALSE, null},
                {" duration(\"PT24H\") in [ duration(\"P1Y2M\") .. duration(\"P1Y3M\")] ", null, FEELEvent.Severity.ERROR},

                // instance of
                {"10 instance of number", Boolean.TRUE , null},
                {"\"foo\" instance of string", Boolean.TRUE , null},
                {"date(\"2016-08-11\") instance of date", Boolean.TRUE , null},
                {"time(\"23:59:00\") instance of time", Boolean.TRUE , null},
                {"date and time(\"2016-07-29T05:48:23.765-05:00\") instance of date and time", Boolean.TRUE , null},
                {"duration( \"P2Y2M\" ) instance of duration", Boolean.TRUE , null},
                {"true instance of boolean", Boolean.TRUE , null},
                {"< 10 instance of range", Boolean.TRUE , null},
                {"[10..20) instance of range", Boolean.TRUE , null},
                {"[10, 20, 30] instance of list", Boolean.TRUE , null},
                {"{ foo : \"foo\" } instance of context", Boolean.TRUE , null},
                {"null instance of any", Boolean.FALSE , null}, // See FEEL spec table 49.
                {"null instance of string", Boolean.FALSE , null},  // See FEEL spec table 49.
                {"\"foo\" instance of any", Boolean.TRUE , null},
                {"10 instance of any", Boolean.TRUE , null},
                {"duration instance of function", Boolean.TRUE , null},
                {"[1,2,3] instance of list<number>", Boolean.TRUE , null},
                {"[1,2,3] instance of list < number>", Boolean.TRUE , null},
                {"[1,2,\"asd\"] instance of list<number>", Boolean.FALSE , null},
                {"123 instance of context<name:string, age:number>", Boolean.FALSE , null},
                {"{ name : \"John\" } instance of context<name:string, age:number>", Boolean.FALSE , null},
                {"{ name : \"John\", age : 47 } instance of context<name:string, age:number>", Boolean.TRUE , null},
                {"{ name : \"John\", age : 47, country : \"IT\" } instance of context<name:string, age:number>", Boolean.TRUE , null},
                {"123 instance of function<number> -> boolean", Boolean.FALSE , null},
                {"{ f : function(age) true,              r : f instance of function<number> -> Any }.r", Boolean.TRUE , null},
                {"{ f : function(age) true,              r : f instance of function<Any> -> Any }.r", Boolean.TRUE , null},
                {"{ f : function(age) true,              r : f instance of function<Any, Any> -> Any }.r", Boolean.FALSE , null},
                {"{ f : function(age : number) age > 18, r : f instance of function<number> -> Any }.r", Boolean.TRUE , null},
                {"{ f : function(age : number) age > 18, r : f instance of function<Any> -> Any }.r", Boolean.FALSE , null},
                {"{ f : function(age : Any) true       , r : f instance of function<number> -> Any }.r", Boolean.TRUE , null},
        };
        return addAdditionalParameters(cases, false);
    }
}
