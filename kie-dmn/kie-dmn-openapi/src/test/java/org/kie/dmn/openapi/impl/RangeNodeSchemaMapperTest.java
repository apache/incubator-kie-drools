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

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.impl.FEELBuilder;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.getBaseNodes;

class RangeNodeSchemaMapperTest {

    private static final FEEL feel = FEELBuilder.builder().build();

    @Test
    void evaluateUnaryTestsForNumberRange() {
        List<String> toRange = Arrays.asList("(>1)", "(<=10)");
        List<RangeNode> ranges = getBaseNodes(toRange, RangeNode.class);
        Schema toPopulate = OASFactory.createObject(Schema.class);
        RangeNodeSchemaMapper.populateSchemaFromListOfRanges(toPopulate, ranges);
        assertEquals(BigDecimal.ONE, toPopulate.getMinimum());
        assertTrue(toPopulate.getExclusiveMinimum());
        assertEquals(BigDecimal.TEN, toPopulate.getMaximum());
        assertFalse(toPopulate.getExclusiveMaximum());
    }

    @Test
    void evaluateUnaryTestsForDateRange() {
        List<LocalDate> expectedDates = Arrays.asList(LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1));
        List<String> formattedDates = expectedDates.stream()
                .map(toFormat -> String.format("@\"%s-0%s-0%s\"", toFormat.getYear(), toFormat.getMonthValue(), toFormat.getDayOfMonth()))
                .toList();
        List<String> toRange = Arrays.asList(String.format("(>%s)", formattedDates.get(0)), String.format("(<=%s)",
                                                                                                          formattedDates.get(1)));
        List<RangeNode> ranges = getBaseNodes(toRange, RangeNode.class);

        Schema toPopulate = OASFactory.createObject(Schema.class);
        RangeNodeSchemaMapper.populateSchemaFromListOfRanges(toPopulate, ranges);
        assertEquals(expectedDates.get(0), toPopulate.getExtensions().get(DMNOASConstants.X_DMN_MINIMUM_VALUE));
        assertTrue(toPopulate.getExclusiveMinimum());
        assertEquals(expectedDates.get(1), toPopulate.getExtensions().get(DMNOASConstants.X_DMN_MAXIMUM_VALUE));
        assertFalse(toPopulate.getExclusiveMaximum());
    }

    @Test
    void consolidateRangesForNumberRange() {
        Range lowRange = new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.ONE, null, Range.RangeBoundary.OPEN);
        Range highRange = new RangeImpl(Range.RangeBoundary.OPEN, null, BigDecimal.TEN, Range.RangeBoundary.CLOSED);
        List<RangeNode> ranges = getRangeNodes(lowRange, highRange);
        Range retrieved = RangeNodeSchemaMapper.consolidateRanges(ranges);
        assertThat(retrieved).isNotNull().isEqualTo(new RangeImpl(lowRange.getLowBoundary(), lowRange.getLowEndPoint(),
                                                                  highRange.getHighEndPoint(),
                                                                  highRange.getHighBoundary()));
    }

    @Test
    void consolidateRangesForDateRange() {
        List<LocalDate> expectedDates = Arrays.asList(LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1));
        Range lowRange = new RangeImpl(Range.RangeBoundary.OPEN, expectedDates.get(0), null, Range.RangeBoundary.OPEN);
        Range highRange = new RangeImpl(Range.RangeBoundary.OPEN, null, expectedDates.get(1),
                                        Range.RangeBoundary.CLOSED);
        List<String> formattedDates = expectedDates.stream()
                .map(toFormat -> String.format("@\"%s-0%s-0%s\"", toFormat.getYear(), toFormat.getMonthValue(),
                                               toFormat.getDayOfMonth()))
                .toList();
        List<String> toRange = Arrays.asList(String.format("(%s .. null)", formattedDates.get(0)), String.format(
                "(null .. %s]", formattedDates.get(1)));
        List<RangeNode> ranges = getBaseNodes(toRange, RangeNode.class);
        Range retrieved = RangeNodeSchemaMapper.consolidateRanges(ranges);
        assertThat(retrieved).isNotNull().isEqualTo(new RangeImpl(lowRange.getLowBoundary(), lowRange.getLowEndPoint(),
                                                                  highRange.getHighEndPoint(),
                                                                  highRange.getHighBoundary()));
    }

    @Test
    void consolidateRangesInvalidRepeatedLB() {
        Range lowRange = new RangeImpl(Range.RangeBoundary.CLOSED, 0, null, Range.RangeBoundary.CLOSED);
        Range highRange = new RangeImpl(Range.RangeBoundary.CLOSED, 0, 100, Range.RangeBoundary.CLOSED);
        List<RangeNode> ranges = getRangeNodes(lowRange, highRange);
        Range result = RangeNodeSchemaMapper.consolidateRanges(ranges);
        assertThat(result).isNull();
    }

    @Test
    void consolidateRangesInvalidRepeatedUB() {
        Range lowRange = new RangeImpl(Range.RangeBoundary.CLOSED, null, 50, Range.RangeBoundary.CLOSED);
        Range highRange = new RangeImpl(Range.RangeBoundary.CLOSED, null, 100, Range.RangeBoundary.CLOSED);
        List<RangeNode> ranges = getRangeNodes(lowRange, highRange);
        Range result = RangeNodeSchemaMapper.consolidateRanges(ranges);
        assertThat(result).isNull();
    }

    private List<RangeNode> getRangeNodes(Range lowRange, Range highRange) {
        List<String> toRange = Arrays.asList(lowRange.toString(), highRange.toString());
        return getBaseNodes(toRange, RangeNode.class);
    }

}