package org.kie.dmn.openapi.impl;


import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.Test;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.openapi.impl.FEELSchemaEnum.checkEvaluatedUnaryTestsForNull;
import static org.kie.dmn.openapi.impl.FEELSchemaEnum.checkEvaluatedUnaryTestsForTypeConsistency;
import static org.kie.dmn.openapi.impl.FEELSchemaEnum.evaluateUnaryTests;
import static org.kie.dmn.openapi.impl.FEELSchemaEnum.parseRangeableValuesIntoSchema;
import static org.kie.dmn.openapi.impl.FEELSchemaEnum.parseValuesIntoSchema;

public class FEELSchemaEnumTest {

    private static final FEEL feel = FEEL.newInstance();

    @Test
    public void testParseValuesIntoSchemaWithoutNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<String> expectedStrings = Arrays.asList("ONE", "TWO");
        List<Object> toEnum = expectedStrings.stream().map(toMap -> String.format("\"%s\"", toMap)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap.toString())).toList());
        expression += ", count (?) > 1";
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        parseValuesIntoSchema(toPopulate, unaryTests);
        assertNull(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedStrings.size(), toPopulate.getEnumeration().size());
        expectedStrings.forEach(expectedString -> assertTrue(toPopulate.getEnumeration().contains(expectedString)));
    }

    @Test
    public void testParseValuesIntoSchemaWithNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<String> expectedStrings = Arrays.asList(null, "ONE", "TWO");
        List<Object> toEnum = expectedStrings.stream().map(toFormat -> toFormat == null ? "null": String.format("\"%s\"", toFormat)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap.toString())).toList());
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        parseValuesIntoSchema(toPopulate, unaryTests);
        assertTrue(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedStrings.size(), toPopulate.getEnumeration().size());
        expectedStrings.stream().filter(Objects::nonNull).forEach(expectedString -> assertTrue(toPopulate.getEnumeration().contains(expectedString)));
    }

    @Test
    public void testParseRangeableValuesIntoSchemaNumberWithoutNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<Number> expectedNumbers = Arrays.asList(1, 2);
        String expression = String.join(",", expectedNumbers.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        parseRangeableValuesIntoSchema(toPopulate, unaryTests, Number.class);
        assertNull(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedNumbers.size(), toPopulate.getEnumeration().size());
        List<Object> expected = Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2));
        expected.forEach(expectedNumber -> assertTrue(toPopulate.getEnumeration().contains(expectedNumber)));
    }

    @Test
    public void testParseRangeableValuesNumberIntoSchemaWithNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<Number> expectedNumbers = Arrays.asList(null, 1, 2);
        String expression = String.join(",", expectedNumbers.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        parseRangeableValuesIntoSchema(toPopulate, unaryTests, Number.class);
        assertTrue(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedNumbers.size(), toPopulate.getEnumeration().size());
        List<Object> expected = Arrays.asList(null, BigDecimal.valueOf(1), BigDecimal.valueOf(2));
        expected.stream().forEach(expectedNumber -> assertTrue(toPopulate.getEnumeration().contains(expectedNumber)));
    }

    @Test
    public void testParseRangeableValuesIntoSchemaLocalDateWithoutNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<LocalDate> expectedDates = Arrays.asList(LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1));
        List<String> formattedDates = expectedDates.stream()
                .map(toFormat -> String.format("@\"%s-0%s-0%s\"", toFormat.getYear(), toFormat.getMonthValue(), toFormat.getDayOfMonth()))
                .toList();
        String expression = String.join(",", formattedDates.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        parseRangeableValuesIntoSchema(toPopulate, unaryTests, LocalDate.class);
        assertNull(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedDates.size(), toPopulate.getEnumeration().size());
        expectedDates.forEach(expectedDate -> assertTrue(toPopulate.getEnumeration().contains(expectedDate)));
    }

    @Test
    public void testParseRangeableValuesDateIntoSchemaWithNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<LocalDate> expectedDates = Arrays.asList(null, LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1));
        List<String> formattedDates = expectedDates.stream()
                .map(toFormat -> toFormat == null ? "null" : String.format("@\"%s-0%s-0%s\"", toFormat.getYear(), toFormat.getMonthValue(), toFormat.getDayOfMonth()))
                .toList();
        String expression = String.join(",", formattedDates.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        parseRangeableValuesIntoSchema(toPopulate, unaryTests, LocalDate.class);
        assertTrue(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedDates.size(), toPopulate.getEnumeration().size());
        expectedDates.forEach(expectedDate -> assertTrue(toPopulate.getEnumeration().contains(expectedDate)));
    }

    @Test
    public void testConsolidateRangesSucceed() {
        Range lowRange = new RangeImpl(Range.RangeBoundary.CLOSED, 0, null, Range.RangeBoundary.CLOSED);
        Range highRange = new RangeImpl(Range.RangeBoundary.CLOSED, null, 100, Range.RangeBoundary.CLOSED);
        List<Range> list = Arrays.asList(lowRange, highRange);
        Range result = FEELSchemaEnum.consolidateRanges(list);
        assertThat(result).isNotNull().isEqualTo(new RangeImpl(lowRange.getLowBoundary(), lowRange.getLowEndPoint(), highRange.getHighEndPoint(), highRange.getHighBoundary()));
        //
        lowRange = new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(2022, 1, 1), null, Range.RangeBoundary.CLOSED);
        highRange = new RangeImpl(Range.RangeBoundary.CLOSED, null, LocalDate.of(2024, 1, 1), Range.RangeBoundary.CLOSED);
        list = Arrays.asList(lowRange, highRange);
        result = FEELSchemaEnum.consolidateRanges(list);
        assertThat(result).isNotNull().isEqualTo(new RangeImpl(lowRange.getLowBoundary(), lowRange.getLowEndPoint(), highRange.getHighEndPoint(), highRange.getHighBoundary()));
    }

    @Test
    public void testConsolidateRangesInvalidRepeatedLB() {
        List<Range> list = new ArrayList<>();
        list.add(new RangeImpl(Range.RangeBoundary.CLOSED, 0, null, Range.RangeBoundary.CLOSED));
        list.add(new RangeImpl(Range.RangeBoundary.CLOSED, 0, 100, Range.RangeBoundary.CLOSED));
        Range result = FEELSchemaEnum.consolidateRanges(list);
        assertThat(result).isNull();
    }

    @Test
    public void testConsolidateRangesInvalidRepeatedUB() {
        List<Range> list = new ArrayList<>();
        list.add(new RangeImpl(Range.RangeBoundary.CLOSED, null, 50, Range.RangeBoundary.CLOSED));
        list.add(new RangeImpl(Range.RangeBoundary.CLOSED, null, 100, Range.RangeBoundary.CLOSED));
        Range result = FEELSchemaEnum.consolidateRanges(list);
        assertThat(result).isNull();
    }

    @Test
    public void testEvaluateUnaryTestsSucceed() {
        List<Object> toEnum = Arrays.asList("\"a string\"", 3, "@\"2024-01-01\"");
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap.toString())).toList());
        expression += ", count (?) > 1";
        List<DMNUnaryTest> toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        assertEquals(toEnum.size() + 1, toCheck.size());
        List<Object> retrieved = evaluateUnaryTests(toCheck);
        List<Object> expected = Arrays.asList("a string", BigDecimal.valueOf(3), LocalDate.of(2024, 1, 1));
        assertEquals(expected.size(), retrieved.size());
        expected.forEach(expectedEntry -> assertTrue("Failing asserts for " + expectedEntry, retrieved.stream().anyMatch(ret -> Objects.equals(expectedEntry, ret))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvaluateUnaryTestsFails() {
        List<Object> toEnum = Arrays.asList(null, null, "@\"2024-01-01\"");
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        assertEquals(toEnum.size(), toCheck.size());
        evaluateUnaryTests(toCheck);
    }

    @Test
    public void testCheckEvaluatedUnaryTestsForNullSucceed() {
        List<Object> toCheck = new ArrayList<>(Arrays.asList("1", "2", "3"));
        checkEvaluatedUnaryTestsForNull(toCheck);
        toCheck.add(null);
        checkEvaluatedUnaryTestsForNull(toCheck);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckEvaluatedUnaryTestsForNullFails() {
        List<Object> toCheck = new ArrayList<>(Arrays.asList("1", "2", "3"));
        toCheck.add(null);
        toCheck.add(null);
        checkEvaluatedUnaryTestsForNull(toCheck);
    }

    @Test
    public void testCheckEvaluatedUnaryTestsForTypeConsistencySucceed() {
        List<Object> toCheck = Arrays.asList("1", "2", "3");
        checkEvaluatedUnaryTestsForTypeConsistency(toCheck);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckEvaluatedUnaryTestsForTypeConsistencyFails() {
        List<Object> toCheck = Arrays.asList("1", "2", 3);
        checkEvaluatedUnaryTestsForTypeConsistency(toCheck);
    }
}