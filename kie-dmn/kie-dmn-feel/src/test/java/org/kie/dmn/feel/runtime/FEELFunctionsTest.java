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
import org.kie.dmn.api.feel.runtime.events.FEELEvent;

public class FEELFunctionsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // constants
                { "string(1.1)", "1.1" , null},
                { "string(null)", null, null},
                { "string(date(\"2016-08-14\"))", "2016-08-14" , null},
                { "string(\"Happy %.0fth birthday, Mr %s!\", 38, \"Doe\")", "Happy 38th birthday, Mr Doe!", null},
                {"number(null, \",\", \".\")", null , FEELEvent.Severity.ERROR},
                { "number(\"1,000.05\", \",\", \".\")", new BigDecimal( "1000.05" ) , null},
                { "number(\"1.000,05\", \".\", \",\")", new BigDecimal( "1000.05" ) , null},
                { "number(\"1000,05\", null, \",\")", new BigDecimal( "1000.05" ) , null},
                { "substring(\"foobar\", 3)", "obar" , null},
                { "substring(\"foobar\", 3, 3)", "oba" , null},
                { "substring(\"foobar\", -2, 1)", "a" , null},
                { "substring(\"foobar\", -2, 5)", "ar" , null},
                { "substring(\"foobar\", 15, 5)", null , FEELEvent.Severity.ERROR},
                { "string length(\"foobar\")", BigDecimal.valueOf( 6 ) , null},
                { "string length(null)", null , FEELEvent.Severity.ERROR},
                { "upper case(\"aBc4\")", "ABC4" , null},
                { "upper case(null)", null , FEELEvent.Severity.ERROR},
                { "lower case(\"aBc4\")", "abc4" , null},
                { "lower case(null)", null , FEELEvent.Severity.ERROR},
                { "substring before( \"foobar\", \"bar\")", "foo" , null},
                { "substring before( \"foobar\", \"xyz\")", "" , null},
                { "substring before( \"foobar\", \"foo\")", "" , null},
                { "substring after( \"foobar\", \"foo\")", "bar" , null},
                { "substring after( \"foobar\", \"xyz\")", "foobar" , null},
                { "substring after( \"foobar\", \"bar\")", "" , null},
                { "contains(\"foobar\", \"ob\")", Boolean.TRUE , null},
                { "contains(\"foobar\", \"of\")", Boolean.FALSE , null},
                { "starts with(\"foobar\", \"of\")", Boolean.FALSE , null},
                { "starts with(\"foobar\", \"fo\")", Boolean.TRUE , null},
                { "ends with(\"foobar\", \"of\")", Boolean.FALSE , null},
                { "ends with(\"foobar\", \"bar\")", Boolean.TRUE , null},
                { "matches(\"foo\", \"[a-z]{3}\")", Boolean.TRUE , null},
                { "matches(\"banana\", \"[a-z]{3}\")", Boolean.TRUE , null},
                { "matches(\"two \\n lines\", \"two.*lines\")", Boolean.FALSE , null},
                { "matches(\"two \\n lines\", \"two.*lines\", \"s\")", Boolean.TRUE , null}, // DOT_ALL flag set by "s"
                { "matches(\"one\\ntwo\\nthree\", \"^two$\")", Boolean.FALSE , null},
                { "matches(\"one\\ntwo\\nthree\", \"^two$\", \"m\")", Boolean.TRUE , null}, // MULTILINE flag set by "m"
                { "matches(\"FoO\", \"foo\")", Boolean.FALSE , null},
                { "matches(\"FoO\", \"foo\", \"i\")", Boolean.TRUE , null}, // CASE_INSENSITIVE flag set by "i"
                { "replace(\"banana\",\"a\",\"o\")", "bonono" , null},
                { "replace(\"banana\",\"(an)+\", \"**\")", "b**a" , null},
                { "replace(\"banana\",\"[aeiouy]\",\"[$0]\")", "b[a]n[a]n[a]" , null},
                { "list contains([1, 2, 3], 2)", Boolean.TRUE , null},
                { "list contains([1, 2, 3], 5)", Boolean.FALSE , null},
                { "count([1, 2, 3])", BigDecimal.valueOf( 3 ) , null},
                { "count( 1, 2, 3 )", BigDecimal.valueOf( 3 ) , null},
                { "min( \"a\", \"b\", \"c\" )", "a" , null},
                { "min([ \"a\", \"b\", \"c\" ])", "a" , null},
                { "max( 1, 2, 3 )", BigDecimal.valueOf( 3 ) , null},
                { "max([ 1, 2, 3 ])", BigDecimal.valueOf( 3 ) , null},
                { "sum( 1, 2, 3 )", BigDecimal.valueOf( 6 ) , null},
                { "sum([ 1, 2, 3 ])", BigDecimal.valueOf( 6 ) , null},
                { "mean( 1, 2, 3 )", BigDecimal.valueOf( 2 ) , null},
                { "mean([ 1, 2, 3 ])", BigDecimal.valueOf( 2 ) , null},
                { "all( true, true, true )", Boolean.TRUE , null},
                { "all([ true, true, true ])", Boolean.TRUE , null},
                { "all( true, true, false )", Boolean.FALSE , null},
                { "all([ false ])", Boolean.FALSE , null},
                { "any( false, true, false )", Boolean.TRUE , null},
                { "any([ false, true, false ])", Boolean.TRUE , null},
                { "any( false )", Boolean.FALSE , null},
                { "any([ false, false, false ])", Boolean.FALSE , null},
                { "sublist( [1, 2, 3, 4, 5 ], 3, 2 )", Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ), null},
                { "sublist( [1, 2, 3, 4, 5 ], -2, 1 )", Arrays.asList( BigDecimal.valueOf( 4 ) ), null},
                { "sublist( [1, 2, 3, 4, 5 ], 4, 3 )", null , FEELEvent.Severity.ERROR},
                { "sublist( [1, 2, 3, 4, 5 ], 6, 3 )", null , FEELEvent.Severity.ERROR},
                { "sublist( [1, 2, 3, 4, 5 ], -6, 3 )", null , FEELEvent.Severity.ERROR},
                { "sublist( [1, 2, 3, 4, 5 ], -5, 3 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) , null},
                { "sublist( [1, 2, 3, 4, 5 ], 1, 3 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) , null},
                { "append( [1, 2], 3, 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) , null},
                { "append( [], 3, 4 )", Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) , null},
                { "append( [1, 2] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ) ) , null},
                { "append( [1, 2], null, 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), null, BigDecimal.valueOf( 4 ) ) , null},
                { "append( null, 1, 2 )", null , FEELEvent.Severity.ERROR},
                { "concatenate( [1, 2], [3] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) , null},
                { "concatenate( [1, 2], 3, [4] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) , null},
                { "concatenate( [1, 2], null )", null , FEELEvent.Severity.ERROR},
                { "insert before( [1, 2, 3], 1, 4 )", Arrays.asList( BigDecimal.valueOf( 4 ), BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) , null},
                { "insert before( [1, 2, 3], 3, 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 4 ), BigDecimal.valueOf( 3 ) ) , null},
                { "insert before( [1, 2, 3], 3, null )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), null, BigDecimal.valueOf( 3 ) ) , null},
                { "insert before( null, 3, 4 )", null , FEELEvent.Severity.ERROR},
                { "insert before( [1, 2, 3], 4, 4 )", null , FEELEvent.Severity.ERROR},
                { "insert before( [1, 2, 3], -3, 4 )", Arrays.asList( BigDecimal.valueOf( 4 ), BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) , null},
                { "insert before( [1, 2, 3], -1, 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 4 ), BigDecimal.valueOf( 3 ) ) , null},
                { "insert before( [1, 2, 3], 0, 4 )", null , FEELEvent.Severity.ERROR},
                { "insert before( [1, 2, 3], -4, 4 )", null , FEELEvent.Severity.ERROR},
                { "remove( [1, 2, 3], 1 )", Arrays.asList( BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) , null},
                { "remove( [1, 2, 3], 3 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ) ) , null},
                { "remove( [1, 2, 3], -1 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ) ) , null},
                { "remove( [1, 2, 3], -3 )", Arrays.asList( BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) , null},
                { "remove( [1, 2, 3], 4 )", null , FEELEvent.Severity.ERROR},
                { "remove( [1, 2, 3], -4 )", null , FEELEvent.Severity.ERROR},
                { "remove( [1, 2, 3], 0 )", null , FEELEvent.Severity.ERROR},
                { "reverse( [1, 2, 3] )", Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 1 ) ) , null},
                { "reverse( null )", null , FEELEvent.Severity.ERROR},
                { "index of( [1, 2, 3, 2], 2 )", Arrays.asList( BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 4 ) ) , null},
                { "index of( [1, 2, null, null], null )", Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) , null},
                { "index of( [1, 2, null, null], 1 )", Arrays.asList( BigDecimal.valueOf( 1 ) ) , null},
                { "index of( null, 1 )", null , FEELEvent.Severity.ERROR},
                { "union( [1, 2, 1], [2, 3], 2, 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) , null},
                { "union( [1, 2, null], 4 )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), null, BigDecimal.valueOf( 4 ) ) , null},
                { "union( null, 4 )", null , FEELEvent.Severity.ERROR},
                { "distinct values( [1, 2, 3, 2, 4] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) , null},
                { "distinct values( [1, 2, null, 2, 4] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), null, BigDecimal.valueOf( 4 ) ) , null},
                { "distinct values( 1 )", Arrays.asList( BigDecimal.valueOf( 1 ) ) , null},
                { "distinct values( null )", null , FEELEvent.Severity.ERROR},
                { "decimal( 1/3, 2 )", new BigDecimal("0.33") , null},
                { "decimal( 1.5, 0 )", new BigDecimal("2") , null},
                { "decimal( 2.5, 0 )", new BigDecimal("2") , null},
                { "decimal( null, 0 )", null , FEELEvent.Severity.ERROR},
                { "floor( 1.5 )", new BigDecimal("1") , null},
                { "floor( -1.5 )", new BigDecimal("-2") , null},
                { "floor( null )", null , FEELEvent.Severity.ERROR},
                { "ceiling( 1.5 )", new BigDecimal("2") , null},
                { "ceiling( -1.5 )", new BigDecimal("-1") , null},
                { "ceiling( null )", null , FEELEvent.Severity.ERROR},
                { "ceiling( n : 1.5 )", new BigDecimal("2") , null},
                { "now()", ZonedDateTime.class , null},
                { "sort( [3, 1, 4, 5, 2], function(x,y) x < y )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ),
                                                                                 BigDecimal.valueOf( 4 ), BigDecimal.valueOf( 5 ) ), null },
                { "sort( [3, 1, 4, 5, 2] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ),
                                                                                 BigDecimal.valueOf( 4 ), BigDecimal.valueOf( 5 ) ), null },
                { "sort( list : [3, 1, 4, 5, 2] )", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ),
                                                                                 BigDecimal.valueOf( 4 ), BigDecimal.valueOf( 5 ) ), null },
                { "sort( [\"c\", \"e\", \"d\", \"a\", \"b\"], function(x,y) x < y )", Arrays.asList( "a", "b", "c", "d", "e" ) , null},
                { "sort( list : [\"c\", \"e\", \"d\", \"a\", \"b\"], precedes : function(x,y) x < y )", Arrays.asList( "a", "b", "c", "d", "e" ) , null},
                { "sort( precedes : function(x,y) x < y, list : [\"c\", \"e\", \"d\", \"a\", \"b\"] )", Arrays.asList( "a", "b", "c", "d", "e" ) , null}
        };
        return Arrays.asList( cases );
    }
}
