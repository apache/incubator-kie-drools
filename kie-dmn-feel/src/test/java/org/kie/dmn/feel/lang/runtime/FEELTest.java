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

package org.kie.dmn.feel.lang.runtime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class FEELTest {

    private static final Map<String, Object> EMPTY_INPUT = Collections.EMPTY_MAP;

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        Object[][] cases = new Object[][] {
                // constants
                { "null", EMPTY_INPUT, null },
                { "true", EMPTY_INPUT, Boolean.TRUE },
                { "false", EMPTY_INPUT, Boolean.FALSE },
                // dash is an unary test that always matches, so for now, returning true.
                // have to double check to know if this is not the case
                { "-", EMPTY_INPUT, UnaryTest.class },
                { "50", EMPTY_INPUT, new BigDecimal( "50" ) },
                { "-50", EMPTY_INPUT, new BigDecimal( "-50" ) },
                { "50.872", EMPTY_INPUT, new BigDecimal( "50.872" ) },
                { "-50.567", EMPTY_INPUT, new BigDecimal( "-50.567" ) },
                // quotes are a syntactical markup character for strings, so they disappear when the expression is evaluated
                { "\"foo bar\"", EMPTY_INPUT, "foo bar" },
                { "\"\"", EMPTY_INPUT, "" },

                // math operations
                { "10+5", EMPTY_INPUT, BigDecimal.valueOf( 15 ) },
                { "-10 + -5", EMPTY_INPUT, BigDecimal.valueOf( -15 ) },
                { "(-10) + (-5)", EMPTY_INPUT, BigDecimal.valueOf( -15 ) },
                { "10-5", EMPTY_INPUT, BigDecimal.valueOf( 5 ) },
                { "-10 - -5", EMPTY_INPUT, BigDecimal.valueOf( -5 ) },
                { "(-10) - (-5)", EMPTY_INPUT, BigDecimal.valueOf( -5 ) },
                { "(10 + 20) - (-5 + 3)", EMPTY_INPUT, BigDecimal.valueOf( 32 ) },
                { "10*5", EMPTY_INPUT, BigDecimal.valueOf( 50 ) },
                { "-10 * -5", EMPTY_INPUT, BigDecimal.valueOf( 50 ) },
                { "(-10) * (-5)", EMPTY_INPUT, BigDecimal.valueOf( 50 ) },
                { "(10 + 20) * (-5 * 3)", EMPTY_INPUT, BigDecimal.valueOf( -450 ) },
                { "10/5", EMPTY_INPUT, BigDecimal.valueOf( 2 ) },
                { "-10 / -5", EMPTY_INPUT, BigDecimal.valueOf( 2 ) },
                { "(-10) / (-5)", EMPTY_INPUT, BigDecimal.valueOf( 2 ) },
                { "(10 + 20) / (-5 * 3)", EMPTY_INPUT, BigDecimal.valueOf( -2 ) },
                { "(10 + 20) / 0", EMPTY_INPUT, null },
                { "10 ** 5", EMPTY_INPUT, BigDecimal.valueOf( 100000 ) },
                { "(5+2) ** 5", EMPTY_INPUT, BigDecimal.valueOf( 16807 ) },
                { "5+2 ** 5", EMPTY_INPUT, BigDecimal.valueOf( 37 ) },
                { "5+2 ** 5+3", EMPTY_INPUT, BigDecimal.valueOf( 40 ) },
                { "5+2 ** (5+3)", EMPTY_INPUT, BigDecimal.valueOf( 261 ) },
                { "10 + null", EMPTY_INPUT, null },
                { "null + 10", EMPTY_INPUT, null },
                { "10 - null", EMPTY_INPUT, null },
                { "null - 10", EMPTY_INPUT, null },
                { "10 * null", EMPTY_INPUT, null },
                { "null * 10", EMPTY_INPUT, null },
                { "10 / null", EMPTY_INPUT, null },
                { "null / 10", EMPTY_INPUT, null },
                { "10 + 20 / -5 - 3", EMPTY_INPUT, BigDecimal.valueOf( 3 ) },
                { "10 + 20 / ( -5 - 3 )", EMPTY_INPUT, BigDecimal.valueOf( 7.5 ) },
                // string concatenation
                { "\"foo\"+\"bar\"", EMPTY_INPUT, "foobar" },

                // ternary logic operations as per the spec
                { "true and true", EMPTY_INPUT, Boolean.TRUE },
                { "true and false", EMPTY_INPUT, Boolean.FALSE },
                { "true and null", EMPTY_INPUT,  null },
                { "false and true", EMPTY_INPUT, Boolean.FALSE },
                { "false and false", EMPTY_INPUT, Boolean.FALSE },
                { "false and null", EMPTY_INPUT, Boolean.FALSE },
                { "null and true", EMPTY_INPUT, null },
                { "null and false", EMPTY_INPUT, Boolean.FALSE },
                { "null and null", EMPTY_INPUT, null },
                { "true or true", EMPTY_INPUT, Boolean.TRUE },
                { "true or false", EMPTY_INPUT, Boolean.TRUE },
                { "true or null", EMPTY_INPUT,  Boolean.TRUE },
                { "false or true", EMPTY_INPUT, Boolean.TRUE },
                { "false or false", EMPTY_INPUT, Boolean.FALSE },
                { "false or null", EMPTY_INPUT, null },
                { "null or true", EMPTY_INPUT, Boolean.TRUE },
                { "null or false", EMPTY_INPUT, null },
                { "null or null", EMPTY_INPUT, null },
                // logical operator priority
                { "false and false or true", EMPTY_INPUT, Boolean.TRUE },
                { "false and (false or true)", EMPTY_INPUT, Boolean.FALSE },
                { "true or false and false", EMPTY_INPUT, Boolean.TRUE },
                { "(true or false) and false", EMPTY_INPUT, Boolean.FALSE },

                // number comparisons
                { "10.4 < 20.6", EMPTY_INPUT, Boolean.TRUE },
                { "10.4 <= 20.6", EMPTY_INPUT, Boolean.TRUE },
                { "10.4 = 20.6", EMPTY_INPUT, Boolean.FALSE },
                { "10.4 != 20.6", EMPTY_INPUT, Boolean.TRUE },
                { "10.4 > 20.6", EMPTY_INPUT, Boolean.FALSE },
                { "10.4 >= 20.6", EMPTY_INPUT, Boolean.FALSE },
                { "15.25 = 15.25", EMPTY_INPUT, Boolean.TRUE },
                { "15.25 != 15.25", EMPTY_INPUT, Boolean.FALSE },

                // string comparisons
                { "\"foo\" < \"bar\"", EMPTY_INPUT, Boolean.FALSE },
                { "\"foo\" <= \"bar\"", EMPTY_INPUT, Boolean.FALSE },
                { "\"foo\" = \"bar\"", EMPTY_INPUT, Boolean.FALSE },
                { "\"foo\" != \"bar\"", EMPTY_INPUT, Boolean.TRUE },
                { "\"foo\" > \"bar\"", EMPTY_INPUT, Boolean.TRUE },
                { "\"foo\" >= \"bar\"", EMPTY_INPUT, Boolean.TRUE },
                { "\"foo\" = \"foo\"", EMPTY_INPUT, Boolean.TRUE },
                { "\"foo\" != \"foo\"", EMPTY_INPUT, Boolean.FALSE },

                // boolean comparisons
                { "true = true", EMPTY_INPUT, Boolean.TRUE },
                { "false = false", EMPTY_INPUT, Boolean.TRUE },
                { "false = true", EMPTY_INPUT, Boolean.FALSE },
                { "true = false", EMPTY_INPUT, Boolean.FALSE },
                { "true != true", EMPTY_INPUT, Boolean.FALSE },
                { "false != false", EMPTY_INPUT, Boolean.FALSE },
                { "false != true", EMPTY_INPUT, Boolean.TRUE },
                { "true != false", EMPTY_INPUT, Boolean.TRUE },

                // null comparisons and comparisons between different types
                { "10.4 < null", EMPTY_INPUT, null },
                { "null <= 30.6", EMPTY_INPUT, null },
                { "40 > null", EMPTY_INPUT, null },
                { "null >= 30", EMPTY_INPUT, null },
                { "\"foo\" > null", EMPTY_INPUT, null },
                { "10 > \"foo\"", EMPTY_INPUT, null },
                { "false > \"foo\"", EMPTY_INPUT, null },
                { "\"bar\" != true", EMPTY_INPUT, null },
                { "null = \"bar\"", EMPTY_INPUT, Boolean.FALSE },
                { "false != null", EMPTY_INPUT, Boolean.TRUE },
                { "null = true", EMPTY_INPUT, Boolean.FALSE },
                { "12 = null", EMPTY_INPUT, Boolean.FALSE},
                { "12 != null", EMPTY_INPUT, Boolean.TRUE},
                { "null = null", EMPTY_INPUT, Boolean.TRUE },
                { "null != null", EMPTY_INPUT, Boolean.FALSE },

                // 'not' expression
                { "not( true )", EMPTY_INPUT, Boolean.FALSE },
                { "not( false )", EMPTY_INPUT, Boolean.TRUE },
                { "not( 10 = 3 )", EMPTY_INPUT, Boolean.TRUE },
                { "not( \"foo\" )", EMPTY_INPUT, null },

                // date/time/duration function invocations
                { "date(\"2016-07-29\")", EMPTY_INPUT, DateTimeFormatter.ISO_DATE.parse( "2016-07-29", LocalDate::from ) },
                { "date(\"-0105-07-29\")", EMPTY_INPUT, DateTimeFormatter.ISO_DATE.parse( "-0105-07-29", LocalDate::from ) }, // 105 BC
                { "date(\"2016-15-29\")", EMPTY_INPUT, null },
                { "date( 10 )", EMPTY_INPUT, null },
                { "time(\"23:59:00\")", EMPTY_INPUT, DateTimeFormatter.ISO_TIME.parse( "23:59:00", LocalTime::from ) },
                { "time(\"13:20:00-05:00\")", EMPTY_INPUT, DateTimeFormatter.ISO_TIME.parse( "13:20:00-05:00", OffsetTime::from ) },
                { "time(\"05:48:23.765\")", EMPTY_INPUT, DateTimeFormatter.ISO_TIME.parse( "05:48:23.765", LocalTime::from ) },
                { "date and time(\"2016-07-29T05:48:23.765-05:00\")", EMPTY_INPUT, DateTimeFormatter.ISO_DATE_TIME.parse( "2016-07-29T05:48:23.765-05:00", ZonedDateTime::from ) },
                { "date( 2016, 8, 2 )", EMPTY_INPUT, LocalDate.of( 2016, 8, 2 ) },
                { "date( date and time(\"2016-07-29T05:48:23.765-05:00\") )", EMPTY_INPUT, LocalDate.of( 2016, 7, 29 ) },
                { "time( 14, 52, 25, null )", EMPTY_INPUT, LocalTime.of( 14, 52, 25 ) },
                { "time( 14, 52, 25, duration(\"PT5H\"))", EMPTY_INPUT, OffsetTime.of( 14, 52, 25, 0, ZoneOffset.ofHours( 5 ) ) },
                { "time( date and time(\"2016-07-29T05:48:23.765-05:00\") )", EMPTY_INPUT, OffsetTime.of( 5, 48, 23, 765000000, ZoneOffset.ofHours( -5 ) ) },
                { "duration( \"P2DT20H14M\" )", EMPTY_INPUT, Duration.parse( "P2DT20H14M" ) },
                { "duration( \"P2Y2M\" )", EMPTY_INPUT, Period.parse( "P2Y2M" ) },
                { "duration( \"P26M\" )", EMPTY_INPUT, Period.parse( "P26M" ) },
                { "years and months duration( date(\"2011-12-22\"), date(\"2013-08-24\") )", EMPTY_INPUT, Period.parse( "P1Y8M" ) },

                // if expressions
                { "if true then 10+5 else 10-5", EMPTY_INPUT, BigDecimal.valueOf( 15 ) },
                { "if false then \"foo\" else \"bar\"", EMPTY_INPUT, "bar" },
                { "if date(\"2016-08-02\") > date(\"2015-12-25\") then \"yey\" else \"nay\"", EMPTY_INPUT, "yey" },
                { "if null then \"foo\" else \"bar\"", EMPTY_INPUT, null },

                // between
                { "10 between 5 and 12", EMPTY_INPUT, Boolean.TRUE },
                { "10 between 20 and 30", EMPTY_INPUT, Boolean.FALSE },
                { "\"foo\" between 5 and 12", EMPTY_INPUT, null },
                { "\"foo\" between \"bar\" and \"zap\"", EMPTY_INPUT, Boolean.TRUE },
                { "\"foo\" between null and \"zap\"", EMPTY_INPUT, null },
                { "date(\"2016-08-02\") between date(\"2016-01-01\") and date(\"2016-12-31\")", EMPTY_INPUT, Boolean.TRUE },

                // lists
                { "[ 5, 10+2, \"foo\"+\"bar\", true ]", EMPTY_INPUT, Arrays.asList( BigDecimal.valueOf( 5 ), BigDecimal.valueOf( 12 ), "foobar", Boolean.TRUE ) },

                // in operator
                { "10 in ( 3, 5*2, 20 )", EMPTY_INPUT, Boolean.TRUE },
                { "null in ( 10, \"foo\", null )", EMPTY_INPUT, Boolean.TRUE },
                { "\"foo\" in ( \"bar\", \"baz\" )", EMPTY_INPUT, Boolean.FALSE },
                { "\"foo\" in null", EMPTY_INPUT, null },
                { "\"foo\" in ( 10, false, \"foo\" )", EMPTY_INPUT, Boolean.TRUE },
                { "10 in < 20", EMPTY_INPUT, Boolean.TRUE },
                { "10 in ( > 50, < 5 )", EMPTY_INPUT, Boolean.FALSE },
                { "10 in ( > 5, < -40 )", EMPTY_INPUT, Boolean.TRUE },
                { "null in ( > 20, null )", EMPTY_INPUT, Boolean.TRUE },
                { "null in -", EMPTY_INPUT, Boolean.TRUE },
                { "10 in [5..20]", EMPTY_INPUT, Boolean.TRUE },
                { "10 in [10..20)", EMPTY_INPUT, Boolean.TRUE },
                { "10 in (10..20)", EMPTY_INPUT, Boolean.FALSE },
                { "10 in (5..10)", EMPTY_INPUT, Boolean.FALSE },
                { "10 in ]5..10[", EMPTY_INPUT, Boolean.FALSE },
                { "10 in (5..10]", EMPTY_INPUT, Boolean.TRUE },
                { "\"b\" in (\"a\"..\"z\"]", EMPTY_INPUT, Boolean.TRUE },

                // quantified expressions
                { "some price in [ 80, 11, 110 ] satisfies price > 100", EMPTY_INPUT, Boolean.TRUE },
                { "some price in [ 80, 11, 90 ] satisfies price > 100", EMPTY_INPUT, Boolean.FALSE },
                { "some x in [ 5, 6, 7 ], y in [ 10, 11, 6 ] satisfies x > y", EMPTY_INPUT, Boolean.TRUE },
                { "every price in [ 80, 11, 90 ] satisfies price > 10", EMPTY_INPUT, Boolean.TRUE },
                { "every price in [ 80, 11, 90 ] satisfies price > 70", EMPTY_INPUT, Boolean.FALSE },
                { "some x in [ 5, 6, 7 ], y in [ 10, 11, 12 ] satisfies x < y", EMPTY_INPUT, Boolean.TRUE },

                // contexts
                { "{ first name : \"Bob\", birthday : date(\"1978-09-12\"), salutation : \"Hello \"+first name }", EMPTY_INPUT,
                  new HashMap<String,Object>() {{
                      put( "first name", "Bob" );
                      put( "birthday", LocalDate.of(1978, 9, 12) );
                      put( "salutation", "Hello Bob" );
                }} },
                // nested contexts + qualified name
                { "{ full name : { first name: \"Bob\", last name : \"Doe\" }, birthday : date(\"1978-09-12\"), salutation : \"Hello \"+full name.first name }", EMPTY_INPUT,
                  new HashMap<String,Object>() {{
                      put( "full name", new HashMap<String,Object>() {{
                          put( "first name", "Bob" );
                          put( "last name", "Doe" );
                      }} );
                      put( "birthday", LocalDate.of(1978, 9, 12) );
                      put( "salutation", "Hello Bob" );
                  }} },

                // for
                {"for x in [ 10, 20, 30 ], y in [ 1, 2, 3 ] return x * y", EMPTY_INPUT,
                 Arrays.asList( 10, 20, 30, 20, 40, 60, 30, 60, 90 ).stream().map( x -> BigDecimal.valueOf( x ) ).collect( Collectors.toList() ) },

                // instance of
                {"10 instance of number", EMPTY_INPUT, Boolean.TRUE },
                {"\"foo\" instance of string", EMPTY_INPUT, Boolean.TRUE },
                {"date(\"2016-08-11\") instance of date", EMPTY_INPUT, Boolean.TRUE },
                {"time(\"23:59:00\") instance of time", EMPTY_INPUT, Boolean.TRUE },
                {"date and time(\"2016-07-29T05:48:23.765-05:00\") instance of date and time", EMPTY_INPUT, Boolean.TRUE },
                {"duration( \"P2Y2M\" ) instance of duration", EMPTY_INPUT, Boolean.TRUE },
                {"true instance of boolean", EMPTY_INPUT, Boolean.TRUE },
                {"< 10 instance of unary test", EMPTY_INPUT, Boolean.TRUE },
                {"[10..20) instance of unary test", EMPTY_INPUT, Boolean.TRUE },
                {"[10, 20, 30] instance of list", EMPTY_INPUT, Boolean.TRUE },
                {"{ foo : \"foo\" } instance of context", EMPTY_INPUT, Boolean.TRUE },
                {"null instance of unknown", EMPTY_INPUT, Boolean.TRUE },
                {"duration instance of function", EMPTY_INPUT, Boolean.TRUE },


                //                { "", EMPTY_INPUT,  },

        };
        return Arrays.asList( cases );
    }

    @Parameterized.Parameter(0)
    public String expression;

    @Parameterized.Parameter(1)
    public Map<String, Object> inputVariables;

    @Parameterized.Parameter(2)
    public Object result;

    @Test
    public void testExpression() {
        if( result == null ) {
            assertThat( "Evaluating: '" + expression + "'", FEEL.evaluate( expression, inputVariables ), is( nullValue() ) );
        } else if( result instanceof Class<?> ) {
            assertThat( "Evaluating: '" + expression + "'", FEEL.evaluate( expression, inputVariables ), is( instanceOf( (Class<?>) result ) ) );
        } else {
            assertThat( "Evaluating: '"+expression+"'", FEEL.evaluate( expression, inputVariables ), is( result ) );
        }
    }

//    @Test @Ignore( "Java BigDecimals do not support negative numbers as power. Need to figure out what to do." )
//    public void testMathExprPow2() {
//        assertThat( FEEL.evaluate( "10 ** -5", EMPTY_INPUT ), is( BigDecimal.valueOf( -0.00001 ) ) );
//    }

}
