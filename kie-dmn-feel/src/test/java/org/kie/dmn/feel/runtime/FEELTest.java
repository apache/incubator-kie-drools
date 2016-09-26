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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class FEELTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        Object[][] cases = new Object[][] {
                // constants
                { "null", null },
                { "true", Boolean.TRUE },
                { "false", Boolean.FALSE },
                // dash is an unary test that always matches, so for now, returning true.
                // have to double check to know if this is not the case
                { "-", UnaryTest.class },
                { ".872", new BigDecimal( "0.872" ) },
                { "-.872", new BigDecimal( "-0.872" ) },
                { "+.872", new BigDecimal( "0.872" ) },
                { "50", new BigDecimal( "50" ) },
                { "-50", new BigDecimal( "-50" ) },
                { "+50", new BigDecimal( "50" ) },
                { "50.872", new BigDecimal( "50.872" ) },
                { "-50.567", new BigDecimal( "-50.567" ) },
                { "+50.567", new BigDecimal( "50.567" ) },
                // quotes are a syntactical markup character for strings, so they disappear when the expression is evaluated
                { "\"foo bar\"", "foo bar" },
                { "\"šomeÚnicodeŠtriňg\"", "šomeÚnicodeŠtriňg" },
                { "\"横綱\"", "横綱" },
                { "\"thisIsSomeLongStringThatMustBeProcessedSoHopefullyThisTestPassWithItAndIMustWriteSomethingMoreSoItIsLongerAndLongerAndLongerAndLongerAndLongerTillItIsReallyLong\"", "thisIsSomeLongStringThatMustBeProcessedSoHopefullyThisTestPassWithItAndIMustWriteSomethingMoreSoItIsLongerAndLongerAndLongerAndLongerAndLongerTillItIsReallyLong" },
                { "\"\"", "" },

                // math operations
                { "10+5", BigDecimal.valueOf( 15 ) },
                { "-10 + -5", BigDecimal.valueOf( -15 ) },
                { "(-10) + (-5)", BigDecimal.valueOf( -15 ) },
                { "10-5", BigDecimal.valueOf( 5 ) },
                { "-10 - -5", BigDecimal.valueOf( -5 ) },
                { "(-10) - (-5)", BigDecimal.valueOf( -5 ) },
                { "(10 + 20) - (-5 + 3)", BigDecimal.valueOf( 32 ) },
                { "10*5", BigDecimal.valueOf( 50 ) },
                { "-10 * -5", BigDecimal.valueOf( 50 ) },
                { "(-10) * (-5)", BigDecimal.valueOf( 50 ) },
                { "(10 + 20) * (-5 * 3)", BigDecimal.valueOf( -450 ) },
                { "10/5", BigDecimal.valueOf( 2 ) },
                { "-10 / -5", BigDecimal.valueOf( 2 ) },
                { "(-10) / (-5)", BigDecimal.valueOf( 2 ) },
                { "(10 + 20) / (-5 * 3)", BigDecimal.valueOf( -2 ) },
                { "(10 + 20) / 0", null },
                { "10 ** 5", BigDecimal.valueOf( 100000 ) },
                { "10 ** -5", new BigDecimal( "0.00001" ) },
                { "(5+2) ** 5", BigDecimal.valueOf( 16807 ) },
                { "5+2 ** 5", BigDecimal.valueOf( 37 ) },
                { "5+2 ** 5+3", BigDecimal.valueOf( 40 ) },
                { "5+2 ** (5+3)", BigDecimal.valueOf( 261 ) },
                { "10 + null", null },
                { "null + 10", null },
                { "10 - null", null },
                { "null - 10", null },
                { "10 * null", null },
                { "null * 10", null },
                { "10 / null", null },
                { "null / 10", null },
                { "10 + 20 / -5 - 3", BigDecimal.valueOf( 3 ) },
                { "10 + 20 / ( -5 - 3 )", BigDecimal.valueOf( 7.5 ) },
                { "1.2*10**3", BigDecimal.valueOf( 1200.0 ) },
                // string concatenation
                { "\"foo\"+\"bar\"", "foobar" },

                // ternary logic operations as per the spec
                { "true and true", Boolean.TRUE },
                { "true and false", Boolean.FALSE },
                { "true and null",  null },
                { "false and true", Boolean.FALSE },
                { "false and false", Boolean.FALSE },
                { "false and null", Boolean.FALSE },
                { "null and true", null },
                { "null and false", Boolean.FALSE },
                { "null and null", null },
                { "true or true", Boolean.TRUE },
                { "true or false", Boolean.TRUE },
                { "true or null",  Boolean.TRUE },
                { "false or true", Boolean.TRUE },
                { "false or false", Boolean.FALSE },
                { "false or null", null },
                { "null or true", Boolean.TRUE },
                { "null or false", null },
                { "null or null", null },
                // logical operator priority
                { "false and false or true", Boolean.TRUE },
                { "false and (false or true)", Boolean.FALSE },
                { "true or false and false", Boolean.TRUE },
                { "(true or false) and false", Boolean.FALSE },

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
                { "null != null", Boolean.FALSE },

                // 'not' expression
                { "not( true )", Boolean.FALSE },
                { "not( false )", Boolean.TRUE },
                { "not( 10 = 3 )", Boolean.TRUE },
                { "not( \"foo\" )", null },

                // date/time/duration function invocations
                { "date(\"2016-07-29\")", DateTimeFormatter.ISO_DATE.parse( "2016-07-29", LocalDate::from ) },
                { "date(\"-0105-07-29\")", DateTimeFormatter.ISO_DATE.parse( "-0105-07-29", LocalDate::from ) }, // 105 BC
                { "date(\"2016-15-29\")", null },
                { "date( 10 )", null },
                { "time(\"23:59:00\")", DateTimeFormatter.ISO_TIME.parse( "23:59:00", LocalTime::from ) },
                { "time(\"13:20:00-05:00\")", DateTimeFormatter.ISO_TIME.parse( "13:20:00-05:00", OffsetTime::from ) },
                { "time(\"05:48:23.765\")", DateTimeFormatter.ISO_TIME.parse( "05:48:23.765", LocalTime::from ) },
                { "date and time(\"2016-07-29T05:48:23.765-05:00\")", DateTimeFormatter.ISO_DATE_TIME.parse( "2016-07-29T05:48:23.765-05:00", ZonedDateTime::from ) },
                { "date and time(date(\"2016-07-29\"), time(\"05:48:23.765-05:00\") )", DateTimeFormatter.ISO_DATE_TIME.parse( "2016-07-29T05:48:23.765-05:00", ZonedDateTime::from ) },
                { "date( 2016, 8, 2 )", LocalDate.of( 2016, 8, 2 ) },
                { "date( date and time(\"2016-07-29T05:48:23.765-05:00\") )", LocalDate.of( 2016, 7, 29 ) },
                { "time( 14, 52, 25, null )", LocalTime.of( 14, 52, 25 ) },
                { "time( 14, 52, 25, duration(\"PT5H\"))", OffsetTime.of( 14, 52, 25, 0, ZoneOffset.ofHours( 5 ) ) },
                { "time( date and time(\"2016-07-29T05:48:23.765-05:00\") )", OffsetTime.of( 5, 48, 23, 765000000, ZoneOffset.ofHours( -5 ) ) },
                { "duration( \"P2DT20H14M\" )", Duration.parse( "P2DT20H14M" ) },
                { "duration( \"P2Y2M\" )", Period.parse( "P2Y2M" ) },
                { "duration( \"P26M\" )", Period.parse( "P26M" ) },
                { "years and months duration( date(\"2011-12-22\"), date(\"2013-08-24\") )", Period.parse( "P1Y8M" ) },

                // if expressions
                { "if true then 10+5 else 10-5", BigDecimal.valueOf( 15 ) },
                { "if false then \"foo\" else \"bar\"", "bar" },
                { "if date(\"2016-08-02\") > date(\"2015-12-25\") then \"yey\" else \"nay\"", "yey" },
                { "if null then \"foo\" else \"bar\"", null },

                // between
                { "10 between 5 and 12", Boolean.TRUE },
                { "10 between 20 and 30", Boolean.FALSE },
                { "\"foo\" between 5 and 12", null },
                { "\"foo\" between \"bar\" and \"zap\"", Boolean.TRUE },
                { "\"foo\" between null and \"zap\"", null },
                { "date(\"2016-08-02\") between date(\"2016-01-01\") and date(\"2016-12-31\")", Boolean.TRUE },

                // lists
                { "[ 5, 10+2, \"foo\"+\"bar\", true ]", Arrays.asList( BigDecimal.valueOf( 5 ), BigDecimal.valueOf( 12 ), "foobar", Boolean.TRUE ) },

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

                // quantified expressions
                { "some price in [ 80, 11, 110 ] satisfies price > 100", Boolean.TRUE },
                { "some price in [ 80, 11, 90 ] satisfies price > 100", Boolean.FALSE },
                { "some x in [ 5, 6, 7 ], y in [ 10, 11, 6 ] satisfies x > y", Boolean.TRUE },
                { "every price in [ 80, 11, 90 ] satisfies price > 10", Boolean.TRUE },
                { "every price in [ 80, 11, 90 ] satisfies price > 70", Boolean.FALSE },
                { "some x in [ 5, 6, 7 ], y in [ 10, 11, 12 ] satisfies x < y", Boolean.TRUE },

                // contexts
                { "{ first name : \"Bob\", birthday : date(\"1978-09-12\"), salutation : \"Hello \"+first name }",
                  new HashMap<String,Object>() {{
                      put( "first name", "Bob" );
                      put( "birthday", LocalDate.of(1978, 9, 12) );
                      put( "salutation", "Hello Bob" );
                }} },
                // nested contexts + qualified name
                { "{ full name : { first name: \"Bob\", last name : \"Doe\" }, birthday : date(\"1978-09-12\"), salutation : \"Hello \"+full name.first name }",
                  new HashMap<String,Object>() {{
                      put( "full name", new HashMap<String,Object>() {{
                          put( "first name", "Bob" );
                          put( "last name", "Doe" );
                      }} );
                      put( "birthday", LocalDate.of(1978, 9, 12) );
                      put( "salutation", "Hello Bob" );
                  }} },

                // for
                {"for x in [ 10, 20, 30 ], y in [ 1, 2, 3 ] return x * y",
                 Arrays.asList( 10, 20, 30, 20, 40, 60, 30, 60, 90 ).stream().map( x -> BigDecimal.valueOf( x ) ).collect( Collectors.toList() ) },

                // instance of
                {"10 instance of number", Boolean.TRUE },
                {"\"foo\" instance of string", Boolean.TRUE },
                {"date(\"2016-08-11\") instance of date", Boolean.TRUE },
                {"time(\"23:59:00\") instance of time", Boolean.TRUE },
                {"date and time(\"2016-07-29T05:48:23.765-05:00\") instance of date and time", Boolean.TRUE },
                {"duration( \"P2Y2M\" ) instance of duration", Boolean.TRUE },
                {"true instance of boolean", Boolean.TRUE },
                {"< 10 instance of unary test", Boolean.TRUE },
                {"[10..20) instance of unary test", Boolean.TRUE },
                {"[10, 20, 30] instance of list", Boolean.TRUE },
                {"{ foo : \"foo\" } instance of context", Boolean.TRUE },
                {"null instance of unknown", Boolean.TRUE },
                {"duration instance of function", Boolean.TRUE },

                // path expressions
                {"{ full name: { first name: \"John\", last name: \"Doe\" } }.full name.last name", "Doe" },

                // function definition and invocation
                {"{ hello world : function() \"Hello World!\", message : hello world() }.message", "Hello World!" },
                {"{ is minor : function( person's age ) person's age < 18, bob is minor : is minor( 16 ) }.bob is minor", Boolean.TRUE },
                {"{ maximum : function( v1, v2 ) external { java : { class : \"java.lang.Math\", method signature: \"max(long,long)\" } }, the max : maximum( 10, 20 ) }.the max",
                 BigDecimal.valueOf( 20 ) },

                // named parameters: in this case foo is null
                {"{ is minor : function( foo, person's age ) foo = null and person's age < 18, bob is minor : is minor( person's age : 16 ) }.bob is minor", Boolean.TRUE },

                // filters
                {"[\"a\", \"b\", \"c\"][1]", "a" },
                {"[\"a\", \"b\", \"c\"][-1]", "c" },
                {"[\"a\", \"b\", \"c\"][5]", null },
                {"\"a\"[1]", "a" },
                {"\"a\"[-1]", "a" },
                {"{ a list : [10, 20, 30, 40], second : a list[2] }.second", BigDecimal.valueOf( 20 ) },
                {"[1, 2, 3, 4][item > 2]", Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) },
                {"[ {x:1, y:2}, {x:2, y:3} ][x = 1]", new HashMap<String, Object>(  ) {{ put("x", BigDecimal.valueOf( 1 )); put("y", BigDecimal.valueOf( 2 ));}} },
                {"[ {x:1, y:2}, {x:2, y:3} ].y", Arrays.asList( BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) },

                // unary test invocation
                {"{ is minor : < 18, bob is minor : is minor(16) }.bob is minor", Boolean.TRUE },

        };
        return Arrays.asList( cases );
    }

    @Parameterized.Parameter(0)
    public String expression;

    @Parameterized.Parameter(1)
    public Object result;

    @Test
    public void testExpression() {
        assertResult( expression, result );
    }

}
