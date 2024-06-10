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
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class FEELListsTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {

                { "[ 5, 10+2, \"foo\"+\"bar\", true ]", Arrays.asList( BigDecimal.valueOf( 5 ), BigDecimal.valueOf( 12 ), "foobar", Boolean.TRUE ), null },
                { "[ null ]", Arrays.asList(new Object[] {null}), null },
                { "[ null, null ]", Arrays.asList(null, null), null },
                { "[ null, 47, null ]", Arrays.asList(null, BigDecimal.valueOf(47 ), null), null },

                // Filtering by index
                {"[\"a\", \"b\", \"c\"][1]", "a", null },
                {"[\"a\", \"b\", \"c\"][2]", "b", null },
                {"[\"a\", \"b\", \"c\"][3]", "c", null },
                {"[\"a\", \"b\", \"c\"][-1]", "c", null },
                {"[\"a\", \"b\", \"c\"][-2]", "b", null },
                {"[\"a\", \"b\", \"c\"][-3]", "a", null },
                {"[\"a\", \"b\", \"c\"][4]", null, FEELEvent.Severity.WARN },
                {"[\"a\", \"b\", \"c\"][984]", null, FEELEvent.Severity.WARN },
                {"[\"a\", \"b\", \"c\"][-4]", null, FEELEvent.Severity.WARN },
                {"[\"a\", \"b\", \"c\"][-984]", null, FEELEvent.Severity.WARN },
                {"\"a\"[1]", "a", null },
                {"\"a\"[2]", null, FEELEvent.Severity.WARN },
                {"{L :3, r: L[1]}.r", BigDecimal.valueOf( 3 ), null },
                {"{L :3, r: L[2]}.r", null, FEELEvent.Severity.WARN },
                {"\"a\"[-1]", "a", null },
                {"\"a\"[-2]", null, FEELEvent.Severity.WARN },
                {"{ a list : [10, 20, 30, 40], second : a list[2] }.second", BigDecimal.valueOf( 20 ), null },

                // Filtering by boolean expression
                {"[1, 2, 3, 4][item = 4]", Collections.singletonList(BigDecimal.valueOf(4)), null },
                {"[1, 2, 3, 4][item > 2]", Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ), null },
                {"[1, 2, 3, 4][item > 5]", Collections.emptyList(), null },
                {"[ {x:1, y:2}, {x:2, y:3} ][x = 1]", Collections.singletonList(new HashMap<String, Object>() {{
                    put("x", BigDecimal.valueOf(1));
                    put("y", BigDecimal.valueOf(2));
                }}), null },
                {"[ {x:1, y:2}, {x:2, y:3} ][x > 1]", Collections.singletonList(new HashMap<String, Object>() {
                    {
                        put("x", BigDecimal.valueOf(2));
                        put("y", BigDecimal.valueOf(3));
                    }
                }), null },
                {"[ {x:1, y:2}, {x:2, y:3} ][x = 0]", Collections.emptyList(), null },
                {"{x:false, l:[ {x:1, y:2}, {x:2, y:3} ],r:l[x] }.r", Collections.emptyList(), null },

                // Other filtering
                {"[\"a\", \"b\", \"c\"][a]", Collections.emptyList(), null }, // DROOLS-1679
                {"{ a list : [ { a : false, b : 2 }, { a : true, b : 3 } ], r : a list[a] }.r", Collections.singletonList(new HashMap<String, Object>() {
                    {
                        put("a", true);
                        put("b", BigDecimal.valueOf(3));
                    }
                }), null },
                {"{ a list : [ { a : false, b : 2 }, { a : null, b : 3 }, { b : 4 } ], r : a list[a] }.r", Collections.emptyList(), null },
                {"{ a list : [ \"a\", \"b\", \"c\" ], x : 2, a : a list[x]}.a", "b", null },
                {"{ a list : [ { x : false, y : 2 }, { x : true, y : 3 } ], x : \"asd\", a : a list[x] }.a", Collections.singletonList(new HashMap<String, Object>() {
                    {
                        put("x", true);
                        put("y", BigDecimal.valueOf(3));
                    }
                }), null },
                {"{ a list : [ { x : false, y : 2 }, { x : true, y : 3 } ], x : false, a : a list[x] }.a", Collections.singletonList(new HashMap<String, Object>() {
                    {
                        put("x", true);
                        put("y", BigDecimal.valueOf(3));
                    }
                }), null },
                {"{ a list : [ { x : false, y : 2 }, { x : true, y : 3 } ], x : null, a : a list[x] }.a", Collections.singletonList(new HashMap<String, Object>() {
                    {
                        put("x", true);
                        put("y", BigDecimal.valueOf(3));
                    }
                }), null },
                {"{ people : [ { firstName : \"bob\" }, { firstName : \"max\" } ], result : people[ lastName = null ] }.result", Arrays.asList( new HashMap<String, Object>() {{ put("firstName", "bob"); }}, new HashMap<String, Object>() {{ put("firstName", "max"); }}) , null },
                
                // Selection
                {"[ {x:1, y:2}, {x:2, y:3} ].y", Arrays.asList( BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ), null },
                {"[ {x:1, y:2}, {x:2} ].y", Arrays.asList(BigDecimal.valueOf(2), null), null },
                {"[ {x:1, y:2}, {x:2, y:3} ].z", Arrays.asList(null, null), null },
                {"{ Data: [{v: \"A1\"}, {v: null}, {v: \"C1\"}], r: Data.v }.r", Arrays.asList("A1", null, "C1"), null },
                {"{ Data: [{v: \"A1\"}, {v: null}, {v: \"C1\"}], r: Data[v != \"D1\"].v }.r", Arrays.asList("A1", null, "C1"), null },

                // lists of intervals
                {"[ ( 10 .. 20 ) ]", Collections.singletonList(new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.valueOf(10),
                                                                             BigDecimal.valueOf(20), Range.RangeBoundary.OPEN)), null },
                {"[ ] 10 .. 20 [ ]", Collections.singletonList(new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.valueOf(10),
                                                                             BigDecimal.valueOf(20), Range.RangeBoundary.OPEN)), null },
                {"[ ( duration(\"P1D\") .. duration(\"P10D\") ) ]", Collections.singletonList(new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                            Duration.parse("P10D"), Range.RangeBoundary.OPEN)), null },
                {"[ ( duration(\"P1D\") .. duration(\"P10D\") [ ]", Collections.singletonList(new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                            Duration.parse("P10D"), Range.RangeBoundary.OPEN)), null },
                {"[ ( duration(\"P1D\") .. duration(\"P10D\") ] ]", Collections.singletonList(new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                            Duration.parse("P10D"), Range.RangeBoundary.CLOSED)), null },
                {"[ ] duration(\"P1D\") .. duration(\"P10D\") ) ]", Collections.singletonList(new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                            Duration.parse("P10D"), Range.RangeBoundary.OPEN)), null },
                {"[ ] duration(\"P1D\") .. duration(\"P10D\") [ ]", Collections.singletonList(new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                            Duration.parse("P10D"), Range.RangeBoundary.OPEN)), null },
                {"[ ] duration(\"P1D\") .. duration(\"P10D\") ] ]", Collections.singletonList(new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                            Duration.parse("P10D"), Range.RangeBoundary.CLOSED)), null },
                {"[ [ duration(\"P1D\") .. duration(\"P10D\") ) ]", Collections.singletonList(new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P1D"),
                                                                                                            Duration.parse("P10D"), Range.RangeBoundary.OPEN)), null },
                {"[ [ duration(\"P1D\") .. duration(\"P10D\") [ ]", Collections.singletonList(new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P1D"),
                                                                                                            Duration.parse("P10D"), Range.RangeBoundary.OPEN)), null },
                {"[ [ duration(\"P1D\") .. duration(\"P10D\") ] ]", Collections.singletonList(new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P1D"),
                                                                                                            Duration.parse("P10D"), Range.RangeBoundary.CLOSED)), null },
                {"[ ( duration(\"P1D\") .. duration(\"P10D\") ), ( duration(\"P2D\") .. duration(\"P10D\") )][1]",
                        new RangeImpl( Range.RangeBoundary.OPEN, Duration.parse("P1D"), Duration.parse( "P10D" ), Range.RangeBoundary.OPEN ), null }
        };
        return addAdditionalParameters(cases, false);
    }
}
