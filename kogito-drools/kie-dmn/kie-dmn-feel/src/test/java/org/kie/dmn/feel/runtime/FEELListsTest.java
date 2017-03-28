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
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.junit.runners.Parameterized;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class FEELListsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {

                { "[ 5, 10+2, \"foo\"+\"bar\", true ]", Arrays.asList( BigDecimal.valueOf( 5 ), BigDecimal.valueOf( 12 ), "foobar", Boolean.TRUE ) },

                // Filtering by index
                {"[\"a\", \"b\", \"c\"][1]", "a" },
                {"[\"a\", \"b\", \"c\"][2]", "b" },
                {"[\"a\", \"b\", \"c\"][3]", "c" },
                {"[\"a\", \"b\", \"c\"][-1]", "c" },
                {"[\"a\", \"b\", \"c\"][-2]", "b" },
                {"[\"a\", \"b\", \"c\"][-3]", "a" },
                {"[\"a\", \"b\", \"c\"][4]", null },
                {"[\"a\", \"b\", \"c\"][984]", null },
                {"[\"a\", \"b\", \"c\"][-4]", null },
                {"[\"a\", \"b\", \"c\"][-984]", null },
                {"\"a\"[1]", "a" },
                {"\"a\"[2]", null },
                {"\"a\"[-1]", "a" },
                {"\"a\"[-2]", null },
                {"{ a list : [10, 20, 30, 40], second : a list[2] }.second", BigDecimal.valueOf( 20 ) },

                // Filtering by boolean expression
                {"[1, 2, 3, 4][item = 4]", Arrays.asList( BigDecimal.valueOf( 4 ) ) },
                {"[1, 2, 3, 4][item > 2]", Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) },
                {"[1, 2, 3, 4][item > 5]", Collections.emptyList() },
                {"[ {x:1, y:2}, {x:2, y:3} ][x = 1]", Arrays.asList( new HashMap<String, Object>() {{ put("x", BigDecimal.valueOf( 1 )); put("y", BigDecimal.valueOf( 2 ));}} ) },
                {"[ {x:1, y:2}, {x:2, y:3} ][x > 1]", Arrays.asList( new HashMap<String, Object>() {
                    {
                        put("x", BigDecimal.valueOf( 1 ));
                        put("y", BigDecimal.valueOf( 2 ));
                    }
                    {
                        put("x", BigDecimal.valueOf( 2 ));
                        put("y", BigDecimal.valueOf( 3 ));
                    }
                } ) },
                {"[ {x:1, y:2}, {x:2, y:3} ][x = 0]", Collections.emptyList() },

                // Selection
                {"[ {x:1, y:2}, {x:2, y:3} ].y", Arrays.asList( BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ) ) },
                {"[ {x:1, y:2}, {x:2} ].y", Arrays.asList( BigDecimal.valueOf( 2 ) ) },
                {"[ {x:1, y:2}, {x:2, y:3} ].z", Collections.emptyList() },

                // lists of intervals
                {"[ ( 10 .. 20 ) ]", Arrays.asList( new RangeImpl( Range.RangeBoundary.OPEN, BigDecimal.valueOf( 10 ),
                                                                   BigDecimal.valueOf( 20 ), Range.RangeBoundary.OPEN ) ) },
                {"[ ] 10 .. 20 [ ]", Arrays.asList( new RangeImpl( Range.RangeBoundary.OPEN, BigDecimal.valueOf( 10 ),
                                                                   BigDecimal.valueOf( 20 ), Range.RangeBoundary.OPEN ) ) },
                {"[ ( duration(\"P1D\") .. duration(\"P10D\") ) ]", Arrays.asList( new RangeImpl( Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                  Duration.parse( "P10D" ), Range.RangeBoundary.OPEN ) ) },
                {"[ ( duration(\"P1D\") .. duration(\"P10D\") [ ]", Arrays.asList( new RangeImpl( Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                  Duration.parse( "P10D" ), Range.RangeBoundary.OPEN ) ) },
                {"[ ( duration(\"P1D\") .. duration(\"P10D\") ] ]", Arrays.asList( new RangeImpl( Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                  Duration.parse( "P10D" ), Range.RangeBoundary.CLOSED ) ) },
                {"[ ] duration(\"P1D\") .. duration(\"P10D\") ) ]", Arrays.asList( new RangeImpl( Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                  Duration.parse( "P10D" ), Range.RangeBoundary.OPEN ) ) },
                {"[ ] duration(\"P1D\") .. duration(\"P10D\") [ ]", Arrays.asList( new RangeImpl( Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                  Duration.parse( "P10D" ), Range.RangeBoundary.OPEN ) ) },
                {"[ ] duration(\"P1D\") .. duration(\"P10D\") ] ]", Arrays.asList( new RangeImpl( Range.RangeBoundary.OPEN, Duration.parse("P1D"),
                                                                                                  Duration.parse( "P10D" ), Range.RangeBoundary.CLOSED ) ) },
                {"[ [ duration(\"P1D\") .. duration(\"P10D\") ) ]", Arrays.asList( new RangeImpl( Range.RangeBoundary.CLOSED, Duration.parse("P1D"),
                                                                                                  Duration.parse( "P10D" ), Range.RangeBoundary.OPEN ) ) },
                {"[ [ duration(\"P1D\") .. duration(\"P10D\") [ ]", Arrays.asList( new RangeImpl( Range.RangeBoundary.CLOSED, Duration.parse("P1D"),
                                                                                                  Duration.parse( "P10D" ), Range.RangeBoundary.OPEN ) ) },
                {"[ [ duration(\"P1D\") .. duration(\"P10D\") ] ]", Arrays.asList( new RangeImpl( Range.RangeBoundary.CLOSED, Duration.parse("P1D"),
                                                                                                  Duration.parse( "P10D" ), Range.RangeBoundary.CLOSED ) ) },
                {"[ ( duration(\"P1D\") .. duration(\"P10D\") ), ( duration(\"P2D\") .. duration(\"P10D\") )][1]",
                        new RangeImpl( Range.RangeBoundary.OPEN, Duration.parse("P1D"), Duration.parse( "P10D" ), Range.RangeBoundary.OPEN ) }
        };
        return Arrays.asList( cases );
    }
}
