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
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runners.Parameterized;

public class FEELFunctionsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // constants
                { "string(1.1)", "1.1" },
                { "string(null)", null },
                { "string(date(\"2016-08-14\"))", "2016-08-14" },
                { "number(\"1,000.05\", \",\", \".\")", new BigDecimal( "1000.05" ) },
                { "number(\"1.000,05\", \".\", \",\")", new BigDecimal( "1000.05" ) },
                { "number(\"1000,05\", null, \",\")", new BigDecimal( "1000.05" ) },
                { "substring(\"foobar\", 3)", "obar" },
                { "substring(\"foobar\", 3, 3)", "oba" },
                { "substring(\"foobar\", -2, 1)", "a" },
                { "substring(\"foobar\", -2, 5)", "ar" },
                { "substring(\"foobar\", 15, 5)", null },
                { "string length(\"foobar\")", BigDecimal.valueOf( 6 ) },
                { "string length(null)", null },
                { "upper case(\"aBc4\")", "ABC4" },
                { "upper case(null)", null },
                { "lower case(\"aBc4\")", "abc4" },
                { "lower case(null)", null },
                { "substring before( \"foobar\", \"bar\")", "foo" },
                { "substring before( \"foobar\", \"xyz\")", "" },
                { "substring before( \"foobar\", \"foo\")", "" },
                { "substring after( \"foobar\", \"foo\")", "bar" },
                { "substring after( \"foobar\", \"xyz\")", "foobar" },
                { "substring after( \"foobar\", \"bar\")", "" },
                { "contains(\"foobar\", \"ob\")", Boolean.TRUE },
                { "contains(\"foobar\", \"of\")", Boolean.FALSE },
                { "starts with(\"foobar\", \"of\")", Boolean.FALSE },
                { "starts with(\"foobar\", \"fo\")", Boolean.TRUE },
                { "ends with(\"foobar\", \"of\")", Boolean.FALSE },
                { "ends with(\"foobar\", \"bar\")", Boolean.TRUE },
                { "matches(\"foo\", \"[a-z]{3}\")", Boolean.TRUE },
                { "replace(\"banana\",\"a\",\"o\")", "bonono" },
                { "replace(\"banana\",\"(an)+\", \"**\")", "b**a" },
                { "replace(\"banana\",\"[aeiouy]\",\"[$0]\")", "b[a]n[a]n[a]" },
                { "list contains([1, 2, 3], 2)", Boolean.TRUE },
                { "list contains([1, 2, 3], 5)", Boolean.FALSE },
                { "count([1, 2, 3])", BigDecimal.valueOf( 3 ) },
                { "count( 1, 2, 3 )", BigDecimal.valueOf( 3 ) },
                { "min( \"a\", \"b\", \"c\" )", "a" },
                { "min([ \"a\", \"b\", \"c\" ])", "a" },
                { "max( 1, 2, 3 )", BigDecimal.valueOf( 3 ) },
                { "max([ 1, 2, 3 ])", BigDecimal.valueOf( 3 ) },
                { "sum( 1, 2, 3 )", BigDecimal.valueOf( 6 ) },
                { "sum([ 1, 2, 3 ])", BigDecimal.valueOf( 6 ) },
                { "mean( 1, 2, 3 )", BigDecimal.valueOf( 2 ) },
                { "mean([ 1, 2, 3 ])", BigDecimal.valueOf( 2 ) },
                { "list and( true, true, true )", Boolean.TRUE },
                { "list and([ true, true, true ])", Boolean.TRUE },
                { "list and( true, true, false )", Boolean.FALSE },
                { "list and([ false ])", Boolean.FALSE },
                { "list or( false, true, false )", Boolean.TRUE },
                { "list or([ false, true, false ])", Boolean.TRUE },
                { "list or( false )", Boolean.FALSE },
                { "list or([ false, false, false ])", Boolean.FALSE },
                { "sublist( [1, 2, 3, 4, 5 ], 3, 2 )", Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) )},
                { "sublist( [1, 2, 3, 4, 5 ], -2, 1 )", Arrays.asList( BigDecimal.valueOf( 4 ) )},
                { "sublist( [1, 2, 3, 4, 5 ], 4, 3 )", null },
                { "sublist( [1, 2, 3, 4, 5 ], 6, 3 )", null },
                { "sublist( [1, 2, 3, 4, 5 ], -6, 3 )", null },
                { "sublist( [1, 2, 3, 4, 5 ], -5, 3 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) },
                { "sublist( [1, 2, 3, 4, 5 ], 1, 3 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) },
                { "append( [1, 2], 3, 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) },
                { "append( [], 3, 4 )", Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) },
                { "append( [1, 2] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ) ) },
                { "append( [1, 2], null, 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), null, BigDecimal.valueOf( 4 ) ) },
                { "append( null, 1, 2 )", null },
                { "concatenate( [1, 2], [3] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) },
                { "concatenate( [1, 2], 3, [4] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) },
                { "concatenate( [1, 2], null )", null },
                { "insert before( [1, 2, 3], 1, 4 )", Arrays.asList( BigDecimal.valueOf( 4 ), BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) },
                { "insert before( [1, 2, 3], 3, 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 4 ), BigDecimal.valueOf( 3 ) ) },
                { "insert before( [1, 2, 3], 3, null )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), null, BigDecimal.valueOf( 3 ) ) },
                { "insert before( null, 3, 4 )", null },
                { "insert before( [1, 2, 3], 4, 4 )", null },
                { "insert before( [1, 2, 3], -3, 4 )", Arrays.asList( BigDecimal.valueOf( 4 ), BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) },
                { "insert before( [1, 2, 3], -1, 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 4 ), BigDecimal.valueOf( 3 ) ) },
                { "insert before( [1, 2, 3], 0, 4 )", null },
                { "insert before( [1, 2, 3], -4, 4 )", null },
                { "remove( [1, 2, 3], 1 )", Arrays.asList( BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) },
                { "remove( [1, 2, 3], 3 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ) ) },
                { "remove( [1, 2, 3], -1 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ) ) },
                { "remove( [1, 2, 3], -3 )", Arrays.asList( BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) },
                { "remove( [1, 2, 3], 4 )", null },
                { "remove( [1, 2, 3], -4 )", null },
                { "remove( [1, 2, 3], 0 )", null },
                { "reverse( [1, 2, 3] )", Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 1 ) ) },
                { "reverse( null )", null },
                { "index of( [1, 2, 3, 2], 2 )", Arrays.asList( BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 4 ) ) },
                { "index of( [1, 2, null, null], null )", Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) },
                { "index of( [1, 2, null, null], 1 )", Arrays.asList( BigDecimal.valueOf( 1 ) ) },
                { "index of( null, 1 )", null },
                { "union( [1, 2, 1], [2, 3], 2, 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) },
                { "union( [1, 2, null], 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), null, BigDecimal.valueOf( 4 ) ) },
                { "union( null, 4 )", null },
                { "distinct values( [1, 2, 3, 2, 4] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) },
                { "distinct values( [1, 2, null, 2, 4] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), null, BigDecimal.valueOf( 4 ) ) },
                { "distinct values( 1 )", Arrays.asList( BigDecimal.valueOf( 1 ) ) },
                { "distinct values( null )", null },
                { "decimal( 1/3, 2 )", new BigDecimal("0.33") },
                { "decimal( 1.5, 0 )", new BigDecimal("2") },
                { "decimal( 2.5, 0 )", new BigDecimal("2") },
                { "decimal( null, 0 )", null },
                { "floor( 1.5 )", new BigDecimal("1") },
                { "floor( -1.5 )", new BigDecimal("-2") },
                { "floor( null )", null },
                { "ceiling( 1.5 )", new BigDecimal("2") },
                { "ceiling( -1.5 )", new BigDecimal("-1") },
                { "ceiling( null )", null },
                { "ceiling( n : 1.5 )", new BigDecimal("2") },
                { "now()", ZonedDateTime.class }
        };
        return Arrays.asList( cases );
    }
}
