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
import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.HashMap;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class FEELRangesTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][]{
                {"[1..2]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.CLOSED), null},
                {"[2..1]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(2), BigDecimal.ONE, Range.RangeBoundary.CLOSED), null},
                {"[1..2)", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.OPEN), null},
                {"(1..2]", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.CLOSED), null},
                {"(1..2)", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.OPEN), null},

                {"[\"a\"..\"z\"]", new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.CLOSED), null},
                {"[\"a\"..\"z\")", new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.OPEN), null},
                {"(\"a\"..\"z\"]", new RangeImpl(Range.RangeBoundary.OPEN, "a", "z", Range.RangeBoundary.CLOSED), null},
                {"(\"a\"..\"z\")", new RangeImpl(Range.RangeBoundary.OPEN, "a", "z", Range.RangeBoundary.OPEN), null},
                {"(\"ab\"..\"yz\")", new RangeImpl(Range.RangeBoundary.OPEN, "ab", "yz", Range.RangeBoundary.OPEN), null},
                {"[\"ab\"+\"cd\"..\"yz\")", new RangeImpl(Range.RangeBoundary.CLOSED, "abcd", "yz", Range.RangeBoundary.OPEN), null},
                {"[(\"ab\"+\"cd\")..\"yz\"]", new RangeImpl(Range.RangeBoundary.CLOSED, "abcd", "yz", Range.RangeBoundary.CLOSED), null},

                {"[date(\"1978-09-12\")..date(\"1978-10-13\")]",
                        new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.CLOSED), null},
                {"[date(\"1978-09-12\")..date(\"1978-10-13\"))",
                        new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.OPEN), null},
                {"(date(\"1978-09-12\")..date(\"1978-10-13\")]",
                        new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.CLOSED), null},
                {"(date(\"1978-09-12\")..date(\"1978-10-13\"))",
                        new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.OPEN), null},
                {"[duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\")]",
                        new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED), null},
                {"[duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\"))",
                        new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.OPEN), null},
                {"(duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\")]",
                        new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED), null},
                {"(duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\"))",
                        new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.OPEN), null},

                {"(duration(\"P1Y6M\")..duration(\"P2Y6M\"))",
                        new RangeImpl(Range.RangeBoundary.OPEN,
                                      new ComparablePeriod(Period.parse("P1Y6M")),
                                      new ComparablePeriod(Period.parse("P2Y6M")),
                                      Range.RangeBoundary.OPEN), null},

                {"[1+2..8]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED), null},
                {"[1+2..8)", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.OPEN), null},
                {"(1+2..8]", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED), null},
                {"(1+2..8)", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.OPEN), null},

                {"[3..2+6]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED), null},
                {"[3..2+6)", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.OPEN), null},
                {"(3..2+6]", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED), null},
                {"(3..2+6)", new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.OPEN), null},

                {"[3..(2+6)]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED), null},
                {"[(1+2)..8]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED), null},

                {"[max( 1, 2, 3 )..8]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(3), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED), null},
                {"[max( 1, 2, 3 )+1..8]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(4), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED), null},

                // Not same types, shouldn't compile.
                {"[1..\"cheese\"]", null, FEELEvent.Severity.ERROR},
                {"[1..date(\"1978-09-12\")]", null, FEELEvent.Severity.ERROR},

                {"[([1, 2, 3, 4][4])..8]", new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(4), BigDecimal.valueOf(8), Range.RangeBoundary.CLOSED), null},

                {"{ x: 3, numberrange: [1+x..8]}", new HashMap<String, Object>() {{
                    put("x", BigDecimal.valueOf(3));
                    put("numberrange",
                            new RangeImpl(
                                    Range.RangeBoundary.CLOSED,
                                    BigDecimal.valueOf(4),
                                    BigDecimal.valueOf(8),
                                    Range.RangeBoundary.CLOSED));
                        }}, null},
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
                        }}, null},
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
                        }}, null},
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
                        }}, null},
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
                        }}, null},
                // Example from spec. chapter "10.3.2.7 Ranges"
                {"{ startdate: date(\"1978-09-12\"), enddate: date(\"1978-10-13\"), rangedates: [startdate..enddate] }",
                        new HashMap<String, Object>() {{
                            put("startdate", LocalDate.of(1978, 9, 12));
                            put("enddate", LocalDate.of(1978, 10, 13));
                            put("rangedates", new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.CLOSED));
                        }}, null},
                
                // Table 42:
                {"[1..10].start included", Boolean.TRUE, null},
                {"[1..10].start", new BigDecimal(1), null},
                {"[1..10].end", new BigDecimal(10), null},
                {"[1..10].end included", Boolean.TRUE, null},
                {"(1..10].start included", Boolean.FALSE, null},
                {"(1..10].start", new BigDecimal(1), null},
                {"(1..10].end", new BigDecimal(10), null},
                {"(1..10].end included", Boolean.TRUE, null},
                {"(<=10).start included", Boolean.FALSE, null},
                {"(<=10).start", null, null},
                {"(<=10).end", new BigDecimal(10), null},
                {"(<=10).end included", Boolean.TRUE, null},
                {"(>1).start included", Boolean.FALSE, null},
                {"(>1).start", new BigDecimal(1), null},
                {"(>1).end", null, null},
                {"(>1).end included", Boolean.FALSE, null},
        };
        return addAdditionalParameters(cases, false);
    }
}
