/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.openapi.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.Test;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTestImpl;
import org.kie.dmn.feel.runtime.impl.RangeImpl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.openapi.impl.DMNUnaryTestsMapper.getUnaryEvaluationNodesFromUnaryTests;
import static org.kie.dmn.openapi.impl.RangeNodeSchemaMapper.populateSchemaFromListOfRanges;


public class RangeNodeSchemaMapperTest {

    private static final FEEL feel = FEEL.newInstance();

    @Test
    public void testEvaluateUnaryTestsForNumberRange() {
        List<Object> toEnum = Arrays.asList("(>1)", "(<=10)");
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> toCheck =
                feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        List<RangeNode> ranges = getUnaryEvaluationNodesFromUnaryTests(toCheck)
                .stream()
                .map(RangeNode.class::cast)
                .toList();
        Schema schema = OASFactory.createObject(Schema.class);
        populateSchemaFromListOfRanges(schema, ranges);
        assertEquals(BigDecimal.ONE, schema.getMinimum());
        assertTrue(schema.getExclusiveMinimum());
        assertEquals(BigDecimal.TEN, schema.getMaximum());
        assertFalse(schema.getExclusiveMaximum());
    }

    @Test
    public void testEvaluateUnaryTestsForDateRange() {
        List<LocalDate> expectedDates = Arrays.asList(LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1));
        List<String> formattedDates = expectedDates.stream()
                .map(toFormat -> String.format("@\"%s-0%s-0%s\"", toFormat.getYear(), toFormat.getMonthValue(), toFormat.getDayOfMonth()))
                .toList();
        List<Object> toRange = Arrays.asList( String.format("(>%s)", formattedDates.get(0)), String.format("(<=%s)", formattedDates.get(1)));
        String expression = String.join(",", toRange.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();

        FEEL feelInstance = FEEL.newInstance();
        List<Range> toReturn = unaryTests.stream()
                .map(UnaryTestImpl.class::cast)
                .map(UnaryTestImpl::toString)
                .map(feelInstance::evaluate)
                .map(Range.class::cast)
                .collect(Collectors.toList());

        Range range = consolidateRangesLocal(toReturn);

        List<RangeNode> ranges = getUnaryEvaluationNodesFromUnaryTests(unaryTests)
                .stream()
                .map(RangeNode.class::cast)
                .toList();
        Schema schema = OASFactory.createObject(Schema.class);
        populateSchemaFromListOfRanges(schema, ranges);
    }

    static Range consolidateRangesLocal(List<Range> ranges) {
        boolean consistent = true;
        Range result = new RangeImpl();
        for (Range r : ranges) {
            if (r.getLowEndPoint() != null) {
                if (result.getLowEndPoint() == null) {
                    result = new RangeImpl(r.getLowBoundary(), r.getLowEndPoint(), result.getHighEndPoint(), result.getHighBoundary());
                } else {
                    consistent = false;
                }
            }
            if (r.getHighEndPoint() != null) {
                if (result.getHighEndPoint() == null) {
                    result = new RangeImpl(result.getLowBoundary(), result.getLowEndPoint(), r.getHighEndPoint(), r.getHighBoundary());
                } else {
                    consistent = false;
                }
            }
        }
        return consistent ? result : null;
    }

//    @Test
//    public void testConsolidateRangesSucceed() {
//        Range lowRange = new RangeImpl(Range.RangeBoundary.CLOSED, 0, null, Range.RangeBoundary.CLOSED);
//        Range highRange = new RangeImpl(Range.RangeBoundary.CLOSED, null, 100, Range.RangeBoundary.CLOSED);
//        List<Range> list = Arrays.asList(lowRange, highRange);
//        Range result = RangeNodeSchemaMapper.consolidateRanges(list);
//        assertThat(result).isNotNull().isEqualTo(new RangeImpl(lowRange.getLowBoundary(), lowRange.getLowEndPoint(),
//                                                               highRange.getHighEndPoint(),
//                                                               highRange.getHighBoundary()));
//        //
//        lowRange = new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(2022, 1, 1), null,
//                                 Range.RangeBoundary.CLOSED);
//        highRange = new RangeImpl(Range.RangeBoundary.CLOSED, null, LocalDate.of(2024, 1, 1),
//                                  Range.RangeBoundary.CLOSED);
//        list = Arrays.asList(lowRange, highRange);
//        result = RangeNodeSchemaMapper.consolidateRanges(list);
//        assertThat(result).isNotNull().isEqualTo(new RangeImpl(lowRange.getLowBoundary(), lowRange.getLowEndPoint(),
//                                                               highRange.getHighEndPoint(),
//                                                               highRange.getHighBoundary()));
//    }
//
//    @Test
//    public void testConsolidateRangesInvalidRepeatedLB() {
//        List<Range> list = new ArrayList<>();
//        list.add(new RangeImpl(Range.RangeBoundary.CLOSED, 0, null, Range.RangeBoundary.CLOSED));
//        list.add(new RangeImpl(Range.RangeBoundary.CLOSED, 0, 100, Range.RangeBoundary.CLOSED));
//        Range result = RangeNodeSchemaMapper.consolidateRanges(list);
//        assertThat(result).isNull();
//    }
//
//    @Test
//    public void testConsolidateRangesInvalidRepeatedUB() {
//        List<Range> list = new ArrayList<>();
//        list.add(new RangeImpl(Range.RangeBoundary.CLOSED, null, 50, Range.RangeBoundary.CLOSED));
//        list.add(new RangeImpl(Range.RangeBoundary.CLOSED, null, 100, Range.RangeBoundary.CLOSED));
//        Range result = RangeNodeSchemaMapper.consolidateRanges(list);
//        assertThat(result).isNull();
//    }
}