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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import org.junit.runners.Parameterized;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class FEELRangesTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][]{
                {"[1..2]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.CLOSED)},
                {"[2..1]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(2), BigDecimal.ONE, Range.RangeBoundary.CLOSED)},
                {"[1..2)", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.OPEN)},
                {"(1..2]", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.CLOSED)},
                {"(1..2)", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.OPEN)},

                {"[\"a\"..\"z\"]", new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.CLOSED)},
                {"[\"a\"..\"z\")", new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.OPEN)},
                {"(\"a\"..\"z\"]", new RangeImpl(Range.RangeBoundary.OPEN, "a", "z", Range.RangeBoundary.CLOSED)},
                {"(\"a\"..\"z\")", new RangeImpl(Range.RangeBoundary.OPEN, "a", "z", Range.RangeBoundary.OPEN)},
                {"(\"ab\"..\"yz\")", new RangeImpl(Range.RangeBoundary.OPEN, "ab", "yz", Range.RangeBoundary.OPEN)},
                {"[\"ab\"+\"cd\"..\"yz\")", new RangeImpl(Range.RangeBoundary.CLOSED, "abcd", "yz", Range.RangeBoundary.OPEN)},
                {"[(\"ab\"+\"cd\")..\"yz\"]", new RangeImpl(Range.RangeBoundary.CLOSED, "abcd", "yz", Range.RangeBoundary.CLOSED)},

                {"[date(\"1978-09-12\")..date(\"1978-10-13\")]",
                        new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.CLOSED)},
                {"[date(\"1978-09-12\")..date(\"1978-10-13\"))",
                        new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.OPEN)},
                {"(date(\"1978-09-12\")..date(\"1978-10-13\")]",
                        new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.CLOSED)},
                {"(date(\"1978-09-12\")..date(\"1978-10-13\"))",
                        new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.OPEN)},
                {"[duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\")]",
                        new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED)},
                {"[duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\"))",
                        new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.OPEN)},
                {"(duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\")]",
                        new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED)},
                {"(duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\"))",
                        new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.OPEN)},

                {"[1+2..8]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED)},
                {"[1+2..8)", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.OPEN)},
                {"(1+2..8]", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED)},
                {"(1+2..8)", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.OPEN)},

                {"[3..2+6]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED)},
                {"[3..2+6)", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.OPEN)},
                {"(3..2+6]", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED)},
                {"(3..2+6)", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.OPEN)},

                {"[3..(2+6)]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED)},
                {"[(1+2)..8]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED)},

                {"[max( 1, 2, 3 )..8]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED)},
                {"[max( 1, 2, 3 )+1..8]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(4), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED)},

                // Not same types, shouldn't compile.
                {"[1..\"cheese\"]", null},
                {"[1..date(\"1978-09-12\")]", null},

                {"[([1, 2, 3, 4][4])..8]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(4), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED)},

                {"{ x: 3, numberrange: [1+x..8]}", new HashMap<String, Object>() {{
                    put("x", BigDecimal.valueOf(3));
                    put("numberrange",
                            new RangeImpl(
                                    Range.RangeBoundary.CLOSED,
                                    BigDecimal.valueOf(4),
                                    BigDecimal.valueOf(8),
                                    Range.RangeBoundary.CLOSED));
                        }}},
                {"{ start: 1, end: 2, numberrange: [start..end] }",
                        new HashMap<String, Object>() {{
                            put("start", BigDecimal.ONE);
                            put("end", BigDecimal.valueOf(2));
                            put("numberrange",
                                    new RangeImpl(
                                            Range.RangeBoundary.CLOSED,
                                            BigDecimal.ONE,
                                            BigDecimal.valueOf(2),
                                            Range.RangeBoundary.CLOSED));
                        }}},
                {"{ start: 1, end: max( 1, 2, 3 ), numberrange: [start..end] }",
                        new HashMap<String, Object>() {{
                            put("start", BigDecimal.ONE);
                            put("end", BigDecimal.valueOf(3));
                            put("numberrange",
                                    new RangeImpl(
                                            Range.RangeBoundary.CLOSED,
                                            BigDecimal.ONE,
                                            BigDecimal.valueOf(3),
                                            Range.RangeBoundary.CLOSED));
                        }}},
                {"{ start: max( 1, 2, 3 ) + 1, end: 8, numberrange: [start..end] }",
                        new HashMap<String, Object>() {{
                            put("start", BigDecimal.valueOf(4));
                            put("end", BigDecimal.valueOf(8));
                            put("numberrange",
                                    new RangeImpl(
                                            Range.RangeBoundary.CLOSED,
                                            BigDecimal.valueOf(4),
                                            BigDecimal.valueOf(8),
                                            Range.RangeBoundary.CLOSED));
                        }}},
                {"{ start: \"a\", end: \"z\", charrange: [start..end] }",
                        new HashMap<String, Object>() {{
                            put("start", "a");
                            put("end", "z");
                            put("charrange",
                                    new RangeImpl(
                                            Range.RangeBoundary.CLOSED,
                                            "a",
                                            "z",
                                            Range.RangeBoundary.CLOSED));
                        }}},
                // Example from spec. chapter "10.3.2.7 Ranges"
                {"{ startdate: date(\"1978-09-12\"), enddate: date(\"1978-10-13\"), rangedates: [startdate..enddate] }",
                        new HashMap<String, Object>() {{
                            put("startdate", LocalDate.of(1978, 9, 12));
                            put("enddate", LocalDate.of(1978, 10, 13));
                            put("rangedates", new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.CLOSED));
                        }}}
        };
        return Arrays.asList(cases);
    }
}
